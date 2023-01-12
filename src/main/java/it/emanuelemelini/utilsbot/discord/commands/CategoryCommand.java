package it.emanuelemelini.utilsbot.discord.commands;

import it.emanuelemelini.utilsbot.UtilsBotApplication;
import it.emanuelemelini.utilsbot.sql.models.GuildModel;
import it.emanuelemelini.utilsbot.sql.repos.GuildRepository;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static it.emanuelemelini.utilsbot.UtilsBotApplication.REPLACE_CHAR;

@Log4j2
public class CategoryCommand extends CoreCommand {

	public static final String COMMAND_CATEGORY = "category";

	private final GuildRepository guildRepository;

	public CategoryCommand() {
		super(COMMAND_CATEGORY, "Select this category as the bot category");
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

		if (guildModel == null) {
			guildModel = new GuildModel();
			guildModel.setDiscordId(discordId);
			guildModel.setName(discordGuild.getName());
			guildModel.setDeleted(false);
		}

		Category discordCategory = null;

		for (Category category : event.getGuild()
				.getCategories()) {
			if (category.getChannels()
					.stream()
					.anyMatch(guildChannel -> guildChannel.getIdLong() == event.getChannel()
							.getIdLong())) {
				discordCategory = category;
			}
		}

		if (discordCategory == null)
			return hook.sendMessage("This channel is not inside a category!");

		if (guildModel.getCategoryId() != null && guildModel.getCategoryId()
				.equals(discordCategory.getId()))
			return hook.sendMessage("This category is already the selected utils category!");

		OptionMapping nameOption = event.getOption("name");
		if (nameOption == null)
			return hook.sendMessage("Option error!");

		String voiceName = nameOption.getAsString();
		if (voiceName.isBlank())
			return hook.sendMessage("Insert valid Voice Channel name!");

		if (!voiceName.contains(REPLACE_CHAR))
			return hook.sendMessage("Insert the replace string!");

		guildModel.setVoiceName(voiceName);
		guildModel.setCategoryId(discordCategory.getId());
		guildRepository.save(guildModel);

		return hook.sendMessage("This category is now the selected utils category with default Voice Channel name \"" + voiceName + "\"!");

	}

}
