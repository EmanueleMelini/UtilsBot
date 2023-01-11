package it.emanuelemelini.utilsbot.discord.commands;

import it.emanuelemelini.utilsbot.UtilsBotApplication;
import it.emanuelemelini.utilsbot.sql.models.GuildModel;
import it.emanuelemelini.utilsbot.sql.repos.GuildRepository;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.jetbrains.annotations.NotNull;

import static it.emanuelemelini.utilsbot.UtilsBotApplication.REPLACE_CHAR;

public class ChannelCommand extends CoreCommand {

	private final GuildRepository guildRepository;

	public ChannelCommand() {
		super("channel", "Change the Voice Channel default name");
		addOptionRequired(OptionType.STRING, "name", "Insert the Voice Channel name (use '" + REPLACE_CHAR + "' where the number will be replaced)");

		guildRepository = UtilsBotApplication.getApplicationContext()
				.getBean(GuildRepository.class);

	}

	@Override
	public void exec(@NotNull SlashCommandInteractionEvent event) {
		if (event.getName()
				.equals(name)) {
			slashChannelCommand(event);
		} else {
			return;
		}
	}

	private void slashChannelCommand(@NotNull SlashCommandInteractionEvent event) {
		event.deferReply()
				.setEphemeral(true)
				.queue();
		InteractionHook hook = event.getHook();

		Guild discordGuild = event.getGuild();

		if (discordGuild == null) {
			hook.sendMessage("Guild error!")
					.queue();
			return;
		}

		String discordId = discordGuild.getId();
		GuildModel guildModel = guildRepository.getGuildByDiscordIdAndDeleted(discordId, false);

		if (guildModel == null) {
			hook.sendMessage("Select a category with the /category command first!")
					.queue();
			return;
		}

		OptionMapping nameOption = event.getOption("name");
		if (nameOption == null) {
			hook.sendMessage("Option error!")
					.queue();
			return;
		}

		String voiceName = nameOption.getAsString();
		if (voiceName.isBlank()) {
			hook.sendMessage("Insert valid Voice Channel name!")
					.queue();
			return;
		}

		if (!voiceName.contains(REPLACE_CHAR)) {
			hook.sendMessage("Insert the replace string!")
					.queue();
			return;
		}

		if (guildModel.getVoiceName().equals(voiceName)) {
			hook.sendMessage("The inserted name is the same as the actual one!")
					.queue();
			return;
		}

		guildModel.setVoiceName(voiceName);
		guildRepository.save(guildModel);
		hook.sendMessage("Voice Channel default name changed!")
				.queue();

	}

}
