package it.emanuelemelini.utilsbot.discord.events;

import it.emanuelemelini.utilsbot.UtilsBotApplication;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CoreChannelEvent extends ListenerAdapter {

	public final String name;
	public final String description;
	public boolean bypassMaintenance;

	protected CoreChannelEvent(String name, String description) {
		this.name = name;
		this.description = description;
		this.bypassMaintenance = false;
	}

	@Override
	public void onChannelDelete(@NotNull ChannelDeleteEvent event) {
		if (event.getChannelType()
				.equals(ChannelType.VOICE) && (!UtilsBotApplication.getMaintenance() || bypassMaintenance)) {
			RestAction<?> action = exec(event);
			if (action != null)
				action.queue();
		}
	}

	public abstract @Nullable RestAction<?> exec(ChannelDeleteEvent event);

}
