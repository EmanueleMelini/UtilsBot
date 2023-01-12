package it.emanuelemelini.utilsbot.discord.commands;

import it.emanuelemelini.utilsbot.UtilsBotApplication;
import it.emanuelemelini.utilsbot.sql.models.AuthorModel;
import it.emanuelemelini.utilsbot.sql.repos.AuthorRepository;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AuthorCommand extends CoreCommand {

	private AuthorRepository authorRepository;

	public AuthorCommand() {
		super("author", "Add an author to this Bot");
		addOptionRequired(OptionType.STRING, "id", "Insert the User ID of the Author");
		setDefaultPermission(DefaultMemberPermissions.DISABLED);
		bypassMaintenance = true;
		authorRepository = UtilsBotApplication.getApplicationContext()
				.getBean(AuthorRepository.class);
	}

	@Override
	public @Nullable RestAction<?> exec(@NotNull SlashCommandInteractionEvent event) {
		event.deferReply()
				.setEphemeral(true)
				.queue();

		InteractionHook hook = event.getHook();

		User discordUser = event.getUser();

		AuthorModel authorModel = authorRepository.getAuthorByDiscordIdAndDeleted(discordUser.getId(), false);

		if (authorModel == null)
			return event.getHook()
					.sendMessage("This command can only be executed by Authors, contact the developer!");

		OptionMapping optionMapping = event.getOption("id");
		if (optionMapping == null)
			return hook.sendMessage("Error on mapping!");

		String newAuthorId = optionMapping.getAsString();
		if (newAuthorId.isEmpty())
			return hook.sendMessage("Insert valid User ID!");

		User newAuthorUser;
		try {
			newAuthorUser = event.getJDA()
					.retrieveUserById(newAuthorId)
					.useCache(false)
					.complete();
		} catch (ErrorResponseException e) {
			return hook.sendMessage("No User found with given ID!");
		}

		AuthorModel newAuthorModel = authorRepository.getAuthorByDiscordIdAndDeleted(newAuthorId, false);

		if (newAuthorModel != null)
			return hook.sendMessage("This User is already an Author!");

		newAuthorModel = new AuthorModel();
		newAuthorModel.setDiscordId(newAuthorId);
		newAuthorModel.setName(newAuthorUser.getName() + "#" + newAuthorUser.getDiscriminator());
		newAuthorModel.setDeleted(false);
		authorRepository.save(newAuthorModel);

		return hook.sendMessage("User set as Bot's Author!");

	}

}
