package it.emanuelemelini.utilsbot;

import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

@Log4j2
@SpringBootApplication
public class UtilsBotApplication implements CommandLineRunner, ApplicationContextAware {

	public static final String REPLACE_CHAR = "{num}";

	public final String version;
	private static ConfigurableApplicationContext ctx;
	private static boolean maintenance;

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(UtilsBotApplication.class);
		app.setBannerMode(Banner.Mode.CONSOLE);
		app.run(args);
		UtilsBotApplication.maintenance = false;
	}

	public UtilsBotApplication(@Value("${application.version}") String version) {
		this.version = version;
	}

	public static void restart(@Nullable Runnable then, boolean maintenance) {

		Thread thread = new Thread(() -> {

			log.info("maintenance: " + maintenance);
			ctx.close();
			UtilsBotApplication.maintenance = maintenance;
			SpringApplication app = new SpringApplication(UtilsBotApplication.class);
			app.setBannerMode(Banner.Mode.CONSOLE);
			app.run();

			if (then != null)
				then.run();

		});

		thread.setDaemon(false);
		thread.start();

	}

	@Override
	public void run(String[] args) {
		log.info("Running on version {}", version);
	}

	@Override
	public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
		ctx = (ConfigurableApplicationContext) applicationContext;
	}

	public static ApplicationContext getApplicationContext() {
		return ctx;
	}

	public static ConfigurableApplicationContext getConfigurableApplicationContext() {
		return ctx;
	}

	public static boolean getMaintenance() {
		return maintenance;
	}

}
