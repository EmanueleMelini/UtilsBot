package it.emanuelemelini.utilsbot.discord.events;

import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public abstract class CoreChannelEvent extends ListenerAdapter {

	public final String name;
	public final String description;

	protected CoreChannelEvent(String name, String description) {
		this.name = name;
		this.description = description;
	}

	@Override
	public void onChannelDelete(@NotNull ChannelDeleteEvent event) {
		if (event.getChannelType()
				.equals(ChannelType.VOICE))
			exec(event);
	}

	public abstract void exec(ChannelDeleteEvent event);

}
