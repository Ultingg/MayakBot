package ru.kumkuat.application.GameModule.Controller;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.kumkuat.application.GameModule.Bot.*;
import ru.kumkuat.application.GameModule.Collections.ResponseContainer;

@Component
public class BotController {

    private final KuBot kuBot;
    private final MayakBot mayakBot;
    private final AkhmatovaBot akhmatovaBot;
    private final Brodskiy brodskiy;
    private final MarshakBot marshakBot;

    public BotController(MarshakBot marshakBot, KuBot kuBot, MayakBot mayakBot, AkhmatovaBot akhmatovaBot, Brodskiy brodskiy) {

        this.marshakBot = marshakBot;
        this.kuBot = kuBot;
        this.mayakBot = mayakBot;
        this.akhmatovaBot = akhmatovaBot;
        this.brodskiy = brodskiy;

        try {
            SetWebhook setWebhook = new SetWebhook();
            setWebhook.setUrl(brodskiy.getBotPath());
            brodskiy.execute(setWebhook);
            setWebhook.setUrl(marshakBot.getBotPath());
            marshakBot.execute(setWebhook);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
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
            botsSender.sendLocation(responseContainer.getSendLocation());
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
}
