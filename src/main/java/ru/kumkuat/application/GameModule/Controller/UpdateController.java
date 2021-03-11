package ru.kumkuat.application.GameModule.Controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.kumkuat.application.GameModule.Bot.Brodskiy;
import ru.kumkuat.application.GameModule.Service.ResponseService;

@RestController
public class UpdateController {

    private final Brodskiy brodskiy; // бот слушатель
    private final ResponseService responseService;

    public UpdateController(Brodskiy brodskiy, ResponseService responseService) {
        this.brodskiy = brodskiy;
        this.responseService = responseService;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {

        Message incomingMessage = update.getMessage();
        Long testSceneId = 0L; //тут мы достаем Id сцены исходя из инфы о Юзере
        responseService.checkIncomingMessage(incomingMessage, testSceneId);
        //Тут вся механия распределения сообщений

        return brodskiy.onWebhookUpdateReceived(update); // слушатель возвращает на сервер Телеграмма HTTP 200(OK) с пустым сообщением

    }

}
