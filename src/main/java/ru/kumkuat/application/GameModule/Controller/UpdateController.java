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
import ru.kumkuat.application.GameModule.Service.UserService;

@Slf4j
@RestController
public class UpdateController {

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
        if (update.getMessage() != null) {
            Message incomingMessage = update.getMessage();
            if (commandChecker(incomingMessage)) {
                log.debug("Received throw to Marshak.");
            } else {
                log.debug("Received message.");
                responseService.messageReceiver(incomingMessage);
            }
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

}
