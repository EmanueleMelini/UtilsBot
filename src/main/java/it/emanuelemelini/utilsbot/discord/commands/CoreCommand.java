package it.emanuelemelini.utilsbot.discord.commands;

import it.emanuelemelini.utilsbot.UtilsBotApplication;
import it.emanuelemelini.utilsbot.discord.CommandController;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;

public abstract class CoreCommand extends ListenerAdapter {

	public String name;
	public String description;

	//public final DiscordService service;
	//public final JDA jda;

	public final CommandController commandController;
	public SlashCommandData command;

	public CoreCommand(String name, String description) {
		this.name = name;
		this.description = description;
		command = Commands.slash(name, description);
		command.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_CHANNEL, Permission.MODERATE_MEMBERS));

		commandController = UtilsBotApplication.getApplicationContext().getBean(CommandController.class);
	}

	public void addOption(OptionType optionType, String name, String description) {
		command.addOption(optionType, name, description);
	}

	public void addOptionRequired(OptionType optionType, String name, String description) {
		command.addOption(optionType, name, description, true);
	}

	public SlashCommandData getCommandData() {
		return command;
	}

	@Override
	public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
		if (event.getGuild() == null) {
			event.reply("This bot works only inside Servers!")
					.queue();
		} else {
			exec(event);
		}
	}

	public abstract void exec(@NotNull SlashCommandInteractionEvent event);

}
