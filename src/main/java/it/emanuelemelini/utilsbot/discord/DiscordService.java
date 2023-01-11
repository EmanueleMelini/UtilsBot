package it.emanuelemelini.utilsbot.discord;

import it.emanuelemelini.utilsbot.discord.commands.CategoryCommand;
import it.emanuelemelini.utilsbot.discord.commands.ChannelCommand;
import it.emanuelemelini.utilsbot.discord.events.VoiceChannelDeleteEvent;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Log4j2
@Service
public class DiscordService {

	private final JDA jda;

	public DiscordService(CommandController commandController, @Value("${env.DISCORD_TOKEN}") String token) throws InterruptedException {

		//commandController = new CommandController();

		jda = JDABuilder.create(token, GatewayIntent.GUILD_MESSAGES)
				.setActivity(Activity.listening("people"))
				.addEventListeners(commandController.getListener(), new VoiceChannelDeleteEvent())
				.build()
				.awaitReady();

		log.info("JDA connected");

		String commandListString = jda.updateCommands()
				.addCommands(commandController.register(new CategoryCommand())
						.register(new ChannelCommand())
						.getSlashCommandsData())
				.complete()
				.stream()
				.map(Command::getName)
				.collect(Collectors.joining(", "));

		log.info("[Registered SlashCommands] " + commandListString);

	}

	@Bean(destroyMethod = "shutdown")
	public JDA getJda() {
		return jda;
	}

}
