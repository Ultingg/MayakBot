package ru.kumkuat.application.GameModule.Controller;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.kumkuat.application.GameModule.Bot.KuBot;
import ru.kumkuat.application.GameModule.Bot.MayakBot;

@Component
public class BotController {


    private final KuBot kuBot;
    private final MayakBot mayakBot;
//    Long chatId = 396005041l;

    public BotController(KuBot kuBot, MayakBot mayakBot) {
        this.kuBot = kuBot;
        this.mayakBot = mayakBot;

//        mayakBot.sendMsg(chatId.toString(), "Privet");
    }

    public void chooser(String s, Update update) {
        String chatId = update.getMessage().getChatId().toString();
        switch (s) {
            case ("маяк"):
                mayakSend(chatId);
                break;
            case ("ку"):
                KuSend(chatId);
                break;
        }

    }


    public void mayakSend(String chatId) {
        mayakBot.sendMsg(chatId.toString(), "Privet");

    }

    public void KuSend(String chatId) {
        kuBot.sendMsg(chatId.toString(), "Ты не пройдешь!");
    }
}
