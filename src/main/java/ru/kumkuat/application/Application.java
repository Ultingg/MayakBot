package ru.kumkuat.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.kumkuat.application.GameModule.ApplicationContextProvider;
import ru.kumkuat.application.GameModule.Bot.MarshakBot;

import java.io.BufferedReader;
import java.io.InputStreamReader;


@SpringBootApplication
public class Application implements CommandLineRunner {
    @Autowired
    private ApplicationContextProvider applicationContextProvider;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);


    }
    @Override
    public void run(String...args) throws Exception {

        var bot = applicationContextProvider.getContext().getBean(MarshakBot.class);
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println(bot.getBotUsername());
        System.out.println(bot.getBotToken());
        System.out.println(bot.getBotPath());
    }
}
