package it.emanuelemelini.utilsbot.discord;

import it.emanuelemelini.utilsbot.discord.commands.CoreCommand;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class CommandController {

	private final ListenerRegister listenerRegister;

	public CommandController() {
		listenerRegister = new ListenerRegister();
	}

	public ListenerRegister getListener() {
		return listenerRegister;
	}

	public <T extends CoreCommand> CommandController register(T command) {
		listenerRegister.insertCommand(command);
		return this;
	}

	public Set<CommandData> getSlashCommandsData() {
		return listenerRegister.getCommandDatas();
	}

	public class ListenerRegister implements EventListener {

		private final Set<EventListener> eventListeners;
		private final Set<CommandData> commandDatas;

		public ListenerRegister() {
			eventListeners = new HashSet<>();
			commandDatas = new HashSet<>();
		}

		public void insertCommand(CoreCommand adapter) {
			eventListeners.add(adapter);
			commandDatas.add(adapter.getCommandData());
		}

		public Set<EventListener> getEventListeners() {
			return eventListeners;
		}

		public Set<CommandData> getCommandDatas() {
			return commandDatas;
		}

		@Override
		public void onEvent(@NotNull GenericEvent event) {
			for (EventListener ev : eventListeners) {
				ev.onEvent(event);
			}
		}

	}

}
