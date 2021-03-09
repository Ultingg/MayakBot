package ru.kumkuat.application.GameModule.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.kumkuat.application.GameModule.Bot.BotsSender;
import ru.kumkuat.application.GameModule.Bot.KuBot;
import ru.kumkuat.application.GameModule.Bot.MayakBot;
import ru.kumkuat.application.GameModule.Collections.ReplyCollection;
import ru.kumkuat.application.GameModule.Collections.ResponseContainer;

@Component
public class BotController {

    private final KuBot kuBot;
    private final MayakBot mayakBot;
    @Autowired
    private ReplyCollection replyCollection;
//    Long chatId = 396005041l;

    public BotController(KuBot kuBot, MayakBot mayakBot ) {
        this.kuBot = kuBot;
        this.mayakBot = mayakBot;

    }


    public void responseResolver(ResponseContainer responseContainer) {
        String botName = responseContainer.getBotName();
        int time = responseContainer.getTimingOfReply();
        Thread oneThread = new Thread();
        try{
            oneThread.sleep(time);} // тут какая-то ахенея
        catch (InterruptedException e) {
            e.getStackTrace();
        }
        if(botName.equals("Mayak")) {
            sendResponseToUser(responseContainer, mayakBot);
        }
        if(botName.equals("Ahmatova")) {
            sendResponseToUser(responseContainer, kuBot);
        }




    }
    private void sendResponseToUser(ResponseContainer responseContainer, BotsSender botsSender) {
        if(responseContainer.hasGeolocation()) {
            botsSender.sendLocation2(responseContainer.getSendLocation());
        }
        if(responseContainer.hasAudio()) {
            botsSender.sendPicture(responseContainer.getSendPhoto());
        }
        if(responseContainer.hasPicture()) {
            botsSender.sendPicture(responseContainer.getSendPhoto());
        }
        if(responseContainer.hasText()) {
            botsSender.sendMessage(responseContainer.getSendMessage());
        }
    }

//    public void mayakSend(String chatId, String message) {
//        mayakBot.sendMsg(chatId, message);
//
//    }
//
//    public void KuSend(String chatId,String message) {
//        kuBot.sendMsg(chatId, message);
//    }

//    public void chooser(String s, Update update) {
//        String chatId = update.getMessage().getChatId().toString();
//        switch (s) {
//            case ("маяк"):
//                mayakSend(chatId, replyCollection.getReply(1L).getTextMessage());
//                break;
//            case ("ку"):
//                KuSend(chatId, replyCollection.getReply(0L).getTextMessage());
//                break;
//            case ("Я - писатель"):
//                mayakSend(chatId, "А по моему ты говно!");
//                break;
//            default:
//                mayakSend(chatId, "Я достаю из широких штанин!!!");
//                break;
//        }
//
//    }
}
