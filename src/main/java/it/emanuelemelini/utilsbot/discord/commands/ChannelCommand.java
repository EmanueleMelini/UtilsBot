package it.emanuelemelini.utilsbot.discord.commands;

import it.emanuelemelini.utilsbot.UtilsBotApplication;
import it.emanuelemelini.utilsbot.sql.models.GuildModel;
import it.emanuelemelini.utilsbot.sql.repos.GuildRepository;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
	public @Nullable RestAction<?> exec(@NotNull SlashCommandInteractionEvent event) {
		event.deferReply()
				.setEphemeral(true)
				.queue();

		InteractionHook hook = event.getHook();

		Guild discordGuild = event.getGuild();

		if (discordGuild == null)
			return hook.sendMessage("Guild error!");

		String discordId = discordGuild.getId();
		GuildModel guildModel = guildRepository.getGuildByDiscordIdAndDeleted(discordId, false);

		if (guildModel == null)
			return hook.sendMessage("Select a category with the /category command first!");

		OptionMapping nameOption = event.getOption("name");
		if (nameOption == null)
			return hook.sendMessage("Option error!");

		String voiceName = nameOption.getAsString();
		if (voiceName.isBlank())
			return hook.sendMessage("Insert valid Voice Channel name!");

		if (!voiceName.contains(REPLACE_CHAR))
			return hook.sendMessage("Insert the replace string!");

		if (guildModel.getVoiceName().equals(voiceName))
			return hook.sendMessage("The inserted name is the same as the actual one!");

		guildModel.setVoiceName(voiceName);
		guildRepository.save(guildModel);

		return hook.sendMessage("Voice Channel default name changed!");

	}

}
