package ru.kumkuat.application.GameModule.Controller;

import org.springframework.web.bind.annotation.RestController;
import ru.kumkuat.application.GameModule.Bot.MarshakBot;

@RestController
public class WebHookController {
    private final MarshakBot marshakBot;

    public WebHookController(MarshakBot marshakBot) {
        this.marshakBot = marshakBot;
    }

//    @RequestMapping(value = "/", method = RequestMethod.POST)
//    public BotApiMethod<?> onUpdateReceiver(@RequestBody Update update){
//        return  marshakBot.onWebhookUpdateReceived(update);
//    }
}

//TODO: два webhooka - два адреса для телеграма
