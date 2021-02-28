package ru.kumkuat.application.GameModule.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.kumkuat.application.GameModule.Bot.KuBot;
import ru.kumkuat.application.GameModule.Bot.MayakBot;
import ru.kumkuat.application.GameModule.Collections.ReplyCollection;

@Component
public class BotController {


    private final KuBot kuBot;
    private final MayakBot mayakBot;
    @Autowired
    private ReplyCollection replyCollection;
//    Long chatId = 396005041l;

    public BotController(KuBot kuBot, MayakBot mayakBot) {
        this.kuBot = kuBot;
        this.mayakBot = mayakBot;

    }

    public void chooser(String s, Update update) {
        String chatId = update.getMessage().getChatId().toString();
        switch (s) {
            case ("маяк"):
                mayakSend(chatId, replyCollection.getReply(1L).getTextMessage());
                break;
            case ("ку"):
                KuSend(chatId, replyCollection.getReply(0L).getTextMessage());
                break;
            case ("Я - писатель"):
                mayakSend(chatId, "А по моему ты говно!");
                break;
            default:
                mayakSend(chatId, "Я достаю из широких штанин!!!");
                break;
        }

    }


    public void mayakSend(String chatId, String message) {
        mayakBot.sendMsg(chatId, message);

    }

    public void KuSend(String chatId,String message) {
        kuBot.sendMsg(chatId, message);
    }
}
