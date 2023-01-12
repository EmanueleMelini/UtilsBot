package it.emanuelemelini.utilsbot.discord.commands;

import it.emanuelemelini.utilsbot.UtilsBotApplication;
import it.emanuelemelini.utilsbot.sql.models.AuthorModel;
import it.emanuelemelini.utilsbot.sql.repos.AuthorRepository;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Log4j2
public class RestartCommand extends CoreCommand {

	private AuthorRepository authorRepository;

	public RestartCommand() {
		super("restart", "Restart UtilsBot");
		setDefaultPermission(DefaultMemberPermissions.DISABLED);
		addOption(OptionType.BOOLEAN, "maintenance", "Restart UtilsBot in maintenance mode");

		authorRepository = UtilsBotApplication.getApplicationContext()
				.getBean(AuthorRepository.class);

		bypassMaintenance = true;
	}

	@Override
	public @Nullable RestAction<?> exec(@NotNull SlashCommandInteractionEvent event) {
		event.deferReply()
				.setEphemeral(true)
				.queue();

		InteractionHook hook = event.getHook();

		AuthorModel authorModel = authorRepository.getAuthorByDiscordIdAndDeleted(event.getUser()
				.getId(), false);

		if (authorModel == null)
			return hook.sendMessage("This command can only be executed by Authors, contact the developer!");

		OptionMapping maintenanceOption = event.getOption("maintenance");
		boolean maintenance = false;
		if (maintenanceOption != null)
			maintenance = maintenanceOption.getAsBoolean();

		hook.sendMessage("Restating UtilsBot" + (maintenance ? " in maintenance mode" : "") + "!")
				.complete();

		boolean finalMaintenance = maintenance;
		String finalId = event.getUser()
				.getId();

		UtilsBotApplication.restart(() -> UtilsBotApplication.getApplicationContext()
				.getBean(JDA.class)
				.retrieveUserById(finalId)
				.complete()
				.openPrivateChannel()
				.complete()
				.sendMessage("UtilsBot restarted" + (finalMaintenance ? " in maintenance mode" : "") + "!")
				.queue(), maintenance);

		return null;

	}

}
