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

        if (update.getMessage() != null || !update.getMessage().getFrom().getIsBot()) {

            FutureTask<Boolean> task = new FutureTask<>(new CallBotResponse(update));
            Thread myThready = new Thread(task);
            myThready.start();
        }
        if(update.getMessage().getFrom().getIsBot()) {
            System.out.println("Bot's id: " + update.getMessage().getFrom().getId());
            System.out.println("Bot's username: " + update.getMessage().getFrom().getUserName());
            System.out.println("Bot's firstname & lastname: " + update.getMessage().getFrom().getFirstName() + " " + update.getMessage().getFrom().getLastName());
        }
        return brodskiy.onWebhookUpdateReceived(update);
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
}
