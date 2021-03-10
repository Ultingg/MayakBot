package ru.kumkuat.application.GameModule.Controller;


import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.kumkuat.application.GameModule.Bot.Brodskiy;

@RestController
public class WebHookController {
        private final Brodskiy brodskiy;


    public WebHookController(Brodskiy brodskiy) {
        this.brodskiy = brodskiy;

    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String onUpdateReceived() {
        System.out.println("Пришло");
        return "Сообщение получил но что с ним делать я не знаю";
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        System.out.println("ПОСТ");
        return brodskiy.onWebhookUpdateReceived(update);
    }
}
