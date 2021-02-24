package ru.kumkuat.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.kumkuat.application.Config.BotController;

@SpringBootApplication
public class Application {


    private static BotController botController;

    public static void main(String[] args) {
SpringApplication.run(Application.class, args);
//
//        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
//        botController= context.getBean(botController.class);
//        botController.send();
    }
}
