package ru.kumkuat.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.kumkuat.application.GameModule.ApplicationContextProvider;
import ru.kumkuat.application.GameModule.Bot.MarshakBot;


@SpringBootApplication
@Slf4j
public class Application implements CommandLineRunner {

    @Autowired
    private ApplicationContextProvider applicationContextProvider;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        var bot = applicationContextProvider.getContext().getBean(MarshakBot.class);
        log.info("===================================== Admin bot is {} =============================", bot.getBotUsername());
        log.info("===================================== Domain path is {} =====================================", bot.getBotPath());
    }
}
