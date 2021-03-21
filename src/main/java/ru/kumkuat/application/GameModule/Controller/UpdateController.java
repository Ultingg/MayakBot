package ru.kumkuat.application.GameModule.Controller;

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

        Message incomingMessage = update.getMessage();
        System.out.println(update.getMessage().getChatId());
        System.out.println(update.getMessage().getFrom().getId());
        System.out.println(update.getMessage().getChat().getUserName());
        System.out.println(update.getMessage().getText());
        // Long testSceneId = 0L; //тут мы достаем Id сцены исходя из инфы о Юзере
            if (commandChecker(incomingMessage)) {
                System.out.println("There was a command!");
            } else {

                responseService.messageReciver(incomingMessage);

            }
        //Тут вся механия распределения сообщений
        return brodskiy.onWebhookUpdateReceived(update); // слушатель возвращает на сервер Телеграмма HTTP 200(OK) с пустым сообщением

    }

    @RequestMapping(value = "/admin", method = RequestMethod.POST)
    public BotApiMethod<?> onUpdateReceiver(@RequestBody Update update) {
        return marshakBot.onWebhookUpdateReceived(update);
    }

    private boolean commandChecker(Message message) {
        boolean result = false;
        if (message.hasText()) {
            String textToCheck = message.getText();
            if (textToCheck.contains("/")) {
                result = true;
                System.out.println("There was a command!");
            }
        }
        return result;
    }

}
