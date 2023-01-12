package it.emanuelemelini.utilsbot.discord.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HelpCommand extends CoreCommand {

	public HelpCommand() {
		super("help", "Get the list of all commands and more information.");
		bypassMaintenance = true;
	}

	@Override
	public @Nullable RestAction<?> exec(@NotNull SlashCommandInteractionEvent event) {
		//TODO: create help message on db;
		event.deferReply().setEphemeral(true).queue();
		return event.getHook().sendMessage("This command is wip!");
	}

}
