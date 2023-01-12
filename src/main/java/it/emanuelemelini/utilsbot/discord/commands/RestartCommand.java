package it.emanuelemelini.utilsbot.discord.commands;

import it.emanuelemelini.utilsbot.UtilsBotApplication;
import it.emanuelemelini.utilsbot.sql.models.AuthorModel;
import it.emanuelemelini.utilsbot.sql.repos.AuthorRepository;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.jetbrains.annotations.NotNull;

@Log4j2
public class RestartCommand extends CoreCommand {

	private AuthorRepository authorRepository;

	public RestartCommand() {
		super("restart", "Restart the bot");
		setDefaultPermission(DefaultMemberPermissions.DISABLED);
		addOption(OptionType.BOOLEAN, "close", "Restart the bot closed");

		authorRepository = UtilsBotApplication.getApplicationContext()
				.getBean(AuthorRepository.class);

		bypassClosed = true;
	}

	@Override
	public void exec(@NotNull SlashCommandInteractionEvent event) {
		if (event.getName()
				.equals(name)) {
			slashStopCommand(event);
		} else {
			return;
		}
	}

	private void slashStopCommand(@NotNull SlashCommandInteractionEvent event) {
		event.deferReply()
				.setEphemeral(true)
				.queue();

		AuthorModel authorModel = authorRepository.getAuthorByDiscordIdAndDeleted(event.getUser()
				.getId(), false);

		if (authorModel == null) {
			event.getHook()
					.sendMessage("This command can only be executed by Authors, contact the developer!")
					.queue();
			return;
		}

		OptionMapping closedOption = event.getOption("close");
		boolean closed = false;
		log.info("event.getOption(\"close\"): " + closedOption);
		if (closedOption != null) {
			closed = closedOption.getAsBoolean();
		}

		event.getHook()
				.sendMessage("Bot shutting down" + (closed ? " closed" : "") + "!")
				.complete();

		boolean finalClosed = closed;
		String finalId = event.getUser().getId();

		UtilsBotApplication.restart(() -> UtilsBotApplication.getApplicationContext()
				.getBean(JDA.class)
				.retrieveUserById(finalId)
				.complete()
				.openPrivateChannel()
				.complete()
				.sendMessage("Bot shut down" + (finalClosed ? " closed" : "") + "!")
				.queue(), closed);

	}

}
