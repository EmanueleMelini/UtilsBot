package it.emanuelemelini.utilsbot;

import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
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
	public static ConfigurableApplicationContext ctx;

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(UtilsBotApplication.class);
		app.setBannerMode(Banner.Mode.CONSOLE);
		app.run(args);
	}

	public UtilsBotApplication(@Value("${application.version}") String version) {
		this.version = version;
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

}
