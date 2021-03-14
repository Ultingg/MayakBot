package ru.kumkuat.application.GameModule.Controller;

import org.springframework.stereotype.Component;
import ru.kumkuat.application.GameModule.Bot.*;
import ru.kumkuat.application.GameModule.Collections.ResponseContainer;

@Component
public class BotController {

    private final KuBot kuBot;
    private final MayakBot mayakBot;
    private final AkhmatovaBot akhmatovaBot;
    private final Brodskiy brodskiy;

    public BotController(KuBot kuBot, MayakBot mayakBot, AkhmatovaBot akhmatovaBot, Brodskiy brodskiy) {
        this.kuBot = kuBot;
        this.mayakBot = mayakBot;
        this.akhmatovaBot = akhmatovaBot;

        this.brodskiy = brodskiy;
    }



    public void responseResolver(ResponseContainer responseContainer) {
        String botName = responseContainer.getBotName();
        int time = responseContainer.getTimingOfReply();
        Thread oneThread = new Thread();
        try {
            oneThread.sleep(time);
        } // тут какая-то ахенея
        catch (InterruptedException e) {
            e.getStackTrace();
        }
        if (botName.equals("Mayakovsky")) {
            sendResponseToUser(responseContainer, mayakBot);
        }
        if (botName.equals("Akhmatova")) {
            sendResponseToUser(responseContainer, akhmatovaBot);
        }
        if (botName.equals("Brodskiy")) {
            sendResponseToUser(responseContainer, brodskiy);
        }
        if (botName.equals("Ku")) {
            sendResponseToUser(responseContainer, kuBot);
        }
    }



    private void sendResponseToUser(ResponseContainer responseContainer, BotsSender botsSender) {
        if (responseContainer.hasGeolocation()) {
            botsSender.sendLocation2(responseContainer.getSendLocation());
        }
        if (responseContainer.hasAudio()) {
            botsSender.sendVoice(responseContainer.getSendVoice());
        }
        if (responseContainer.hasPicture()) {
            botsSender.sendPicture(responseContainer.getSendPhoto());
        }
        if (responseContainer.hasText()) {
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
