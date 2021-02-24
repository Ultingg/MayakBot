package ru.kumkuat.application.Config;

import org.springframework.stereotype.Component;
import ru.kumkuat.application.Bot.KuBot;
import ru.kumkuat.application.Bot.MayakBot;

@Component
public class BotController {


    private final KuBot kuBot;
    private final MayakBot mayakBot;
    Long chatId = 396005041l;

    public BotController(KuBot kuBot, MayakBot mayakBot) {
        this.kuBot = kuBot;
        this.mayakBot = mayakBot;

        mayakBot.sendMsg(chatId.toString(), "Privet");
    }

    public void chooser(String s) {
        switch (s) {
            case ("маяк"):
                mayakSend();
                break;
            case ("ку"):
                KuSend();
                break;
        }

    }


    public void mayakSend() {
        mayakBot.sendMsg(chatId.toString(), "Privet");

    }

    public void KuSend() {
        kuBot.sendMsg(chatId.toString(), "Ты не пройдешь!");
    }
}
