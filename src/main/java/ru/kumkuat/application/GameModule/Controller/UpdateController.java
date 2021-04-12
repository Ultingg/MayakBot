package ru.kumkuat.application.GameModule.Controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.kumkuat.application.GameModule.Bot.Brodskiy;
import ru.kumkuat.application.GameModule.Bot.MarshakBot;
import ru.kumkuat.application.GameModule.Service.ResponseService;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

@Slf4j
@RestController
public class UpdateController {

    class CallBotResponse implements Callable<Boolean> {
        Update update;

        CallBotResponse(Update update) {
            this.update = update;
        }

        @Override
        public Boolean call() throws Exception {
            System.out.println("Привет из побочного потока!");
            Message incomingMessage = update.getMessage();
            if (commandChecker(incomingMessage)) {
                log.debug("Received throw to Marshak.");
                responseService.messageReceiver(incomingMessage, navigationCommandCheck(incomingMessage));
            } else {
                log.debug("Received message.");
                responseService.messageReceiver(incomingMessage, false);
            }
            return true;
        }
    }

    private final MarshakBot marshakBot;
    private final Brodskiy brodskiy; // бот слушатель
    private final ResponseService responseService;

    public UpdateController(MarshakBot marshakBot, Brodskiy brodskiy, ResponseService responseService) {
        this.marshakBot = marshakBot;
        this.brodskiy = brodskiy;
        this.responseService = responseService;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        if (update.getMessage() != null) {
            FutureTask<Boolean> task = new FutureTask<>(new CallBotResponse(update));
            Thread myThready = new Thread(task);
            myThready.start();
        }
        //Тут вся механия распределения сообщений
        return brodskiy.onWebhookUpdateReceived(update); // слушатель возвращает на сервер Телеграмма HTTP 200(OK) с пустым сообщением
    }

    @RequestMapping(value = "/admin", method = RequestMethod.POST)
    public BotApiMethod<?> onUpdateReceiver(@RequestBody Update update) {
        log.debug("Received by Marshak command.");
        return marshakBot.onWebhookUpdateReceived(update);
    }

    private boolean commandChecker(Message message) {
        boolean result = false;
        if (message.hasText()) {
            String textToCheck = message.getText();
            if (textToCheck.contains("/")) {
                result = true;
            }
        }
        return result;
    }

    private boolean navigationCommandCheck(Message message) {
        boolean result = false;
        if (message.hasText()) {
            String textToCheck = message.getText();
            if (textToCheck.contains("/next") || textToCheck.contains("/previous")) {
                result = true;
            }
        }
        return result;
    }

}
