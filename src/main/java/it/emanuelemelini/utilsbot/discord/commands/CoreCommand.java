package it.emanuelemelini.utilsbot.discord.commands;

import it.emanuelemelini.utilsbot.UtilsBotApplication;
import it.emanuelemelini.utilsbot.discord.CommandController;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Log4j2
public abstract class CoreCommand extends ListenerAdapter {

	public String name;
	public String description;

	public boolean bypassMaintenance;

	//public final DiscordService service;
	//public final JDA jda;

	public final CommandController commandController;
	public SlashCommandData command;

	public CoreCommand(String name, String description) {
		this.name = name;
		this.description = description;
		command = Commands.slash(name, description);
		command.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_CHANNEL, Permission.MODERATE_MEMBERS));

		commandController = UtilsBotApplication.getApplicationContext()
				.getBean(CommandController.class);

		bypassMaintenance = false;
	}

	public void addOption(OptionType optionType, String name, String description) {
		command.addOption(optionType, name, description);
	}

	public void addOptionRequired(OptionType optionType, String name, String description) {
		command.addOption(optionType, name, description, true);
	}

	public void setDefaultPermission(@NotNull DefaultMemberPermissions permission) {
		command.setDefaultPermissions(permission);
	}

	public SlashCommandData getCommandData() {
		return command;
	}

	@Override
	public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
		if (event.getGuild() == null) {
			event.reply("This bot works only inside Servers!")
					.queue();
		} else if (UtilsBotApplication.getMaintenance() && !bypassMaintenance) {
			event.reply("This command works only when bot is active!")
					.setEphemeral(true)
					.queue();
		} else if (canExecute(event)) {
			RestAction<?> action = exec(event);
			if (action != null)
				action.queue();
		} else
			return;
	}

	public abstract @Nullable RestAction<?> exec(@NotNull SlashCommandInteractionEvent event);

	public boolean canExecute(@NotNull SlashCommandInteractionEvent event) {
		return event.getName()
				.equals(name);
	}

}
