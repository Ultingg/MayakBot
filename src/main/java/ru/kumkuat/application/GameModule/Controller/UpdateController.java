package ru.kumkuat.application.GameModule.Controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.kumkuat.application.GameModule.Bot.Brodskiy;
import ru.kumkuat.application.GameModule.Bot.MarshakBot;
import ru.kumkuat.application.GameModule.Models.User;
import ru.kumkuat.application.GameModule.Service.ResponseService;
import ru.kumkuat.application.GameModule.Service.TelegramChatService;
import ru.kumkuat.application.GameModule.Service.UserService;

@Slf4j
@RestController
public class UpdateController {

    @Autowired
    private TelegramChatService telegramChatService;
    private final MarshakBot marshakBot;
    private final Brodskiy brodskiy; // бот слушатель
    private final ResponseService responseService;
    private final UserService userService;

    public UpdateController(MarshakBot marshakBot, Brodskiy brodskiy, ResponseService responseService, UserService userService) {
        this.marshakBot = marshakBot;
        this.brodskiy = brodskiy;
        this.responseService = responseService;
        this.userService = userService;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        if (update.getMessage() != null || !update.getMessage().getFrom().getIsBot()) {
            Thread myThready = new Thread(new CallBotResponse(update));
            myThready.start();
        }
        if (update.getMessage().getFrom().getIsBot()) {
            System.out.println("Bot's id: " + update.getMessage().getFrom().getId());
            System.out.println("Bot's username: " + update.getMessage().getFrom().getUserName());
            System.out.println("Bot's firstname & lastname: " + update.getMessage().getFrom().getFirstName() + " " + update.getMessage().getFrom().getLastName());
        }
        return brodskiy.onWebhookUpdateReceived(update);
    }

    @RequestMapping(value = "/admin", method = RequestMethod.POST)
    public BotApiMethod<?> onUpdateReceiver(@RequestBody Update update) {
        log.debug("Received by Marshak command.");
        User user = null;
        if (update.hasMessage() && userService.IsUserExist(update.getMessage().getFrom().getId().longValue())) {
            user = userService.getUser(update.getMessage().getFrom().getId().longValue());
        }
        if (user != null && user.isPlaying() && user.getTelegramUserId().equals(update.getMessage().getChatId())) {
            new Thread(new CallBotResponse(update)).start();
            return null;
        } else {
            return marshakBot.onWebhookUpdateReceived(update);
        }
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

    class CallBotResponse implements Runnable {
        Update update;

        CallBotResponse(Update update) {
            this.update = update;
        }

        @Override
        public void run() {
            System.out.println("Привет из побочного потока!");
            Message incomingMessage = update.getMessage();
            if (commandChecker(incomingMessage)) {
                log.debug("Received throw to Marshak.");
                responseService.messageReceiver(incomingMessage, navigationCommandCheck(incomingMessage));
            } else {
                log.debug("Received message.");
                responseService.messageReceiver(incomingMessage, false);
            }

        }
    }
}
