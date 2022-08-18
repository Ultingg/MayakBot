package ru.kumkuat.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.kumkuat.application.gameModule.ApplicationContextProvider;
import ru.kumkuat.application.gameModule.bot.*;


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
        var mayak = applicationContextProvider.getContext().getBean(MayakBot.class);
        var akhmatova = applicationContextProvider.getContext().getBean(AkhmatovaBot.class);
        var harms = applicationContextProvider.getContext().getBean(Harms.class);
        var brodskiy = applicationContextProvider.getContext().getBean(Brodskiy.class);
        log.info("===================================== Admin bot is {} =============================", bot.getBotUsername());
        log.info("===================================== Mayakovskiy bot is {} =============================", mayak.getBotUsername());
        log.info("===================================== Akhmatova bot is {} =============================", akhmatova.getBotUsername());
        log.info("===================================== Brodskiy bot is {} =============================", brodskiy.getBotUsername());
        log.info("===================================== Harms bot is {} =============================", harms.getBotUsername());
        log.info("===================================== Domain path is {} =====================================", bot.getBotPath());
    }
}
