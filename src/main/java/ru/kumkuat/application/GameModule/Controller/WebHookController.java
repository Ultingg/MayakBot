package ru.kumkuat.application.GameModule.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.kumkuat.application.GameModule.Bot.MarshakBot;

@RestController
public class WebHookController {
    private final MarshakBot marshakBot;

    public WebHookController(MarshakBot marshakBot) {
        this.marshakBot = marshakBot;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public BotApiMethod<?> onUpdateReceiver(@RequestBody Update update){
        return  marshakBot.onWebhookUpdateReceived(update);
    }
}
