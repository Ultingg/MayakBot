package ru.kumkuat.application.GameModule.Controller;

import org.springframework.stereotype.Component;
import ru.kumkuat.application.GameModule.Bot.*;
import ru.kumkuat.application.GameModule.Collections.ResponseContainer;

@Component
public class BotController {

    private final Harms harms;
    private final MayakBot mayakBot;
    private final AkhmatovaBot akhmatovaBot;
    private final Brodskiy brodskiy;

    public BotController(Harms harms, MayakBot mayakBot, AkhmatovaBot akhmatovaBot, Brodskiy brodskiy) {
        this.harms = harms;
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
            sendResponseToUser(responseContainer, harms);
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
