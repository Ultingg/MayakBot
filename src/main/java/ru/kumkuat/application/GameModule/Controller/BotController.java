package ru.kumkuat.application.GameModule.Controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.kumkuat.application.GameModule.Abstract.TelegramWebhookCommandBot;
import ru.kumkuat.application.GameModule.Bot.*;
import ru.kumkuat.application.GameModule.Collections.ResponseContainer;

@Slf4j
@Component
public class BotController {

    private final Harms harms;
    private final MayakBot mayakBot;
    private final AkhmatovaBot akhmatovaBot;
    private final Brodskiy brodskiy;
    private final MarshakBot marshakBot;

    public BotController(MarshakBot marshakBot, Harms harms, MayakBot mayakBot, AkhmatovaBot akhmatovaBot, Brodskiy brodskiy) {
        this.harms = harms;
        this.mayakBot = mayakBot;
        this.akhmatovaBot = akhmatovaBot;
        this.brodskiy = brodskiy;
        this.marshakBot = marshakBot;

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

//    public boolean IsBotsStarting(String UserId){
//        SendMessage checkMessage = new SendMessage();
//        checkMessage.setText("Проверка");
//        harms.execute();
//    }

    public void responseResolver(ResponseContainer responseContainer) {
        String botName = responseContainer.getBotName();
        int time = responseContainer.getTimingOfReply();
        try {
            Thread.currentThread().sleep(time);
        } // тут какая-то ахенея
        catch (InterruptedException e) {
            log.debug("Thread was Interrupted while waiting timing of reply.");
            e.getStackTrace();
        }
        if (botName.equals("Marshak")) {
            sendResponseToUserInPrivate(responseContainer, marshakBot);
            log.debug("BotController processed reply of {}.", "Marshak");
        } else {
            if (botName.equals("Mayakovsky")) {
                sendResponseToUser(responseContainer, mayakBot);
                log.debug("BotController processed reply of {}.", "Myakovskiy");
            }
            if (botName.equals("Akhmatova")) {
                sendResponseToUser(responseContainer, akhmatovaBot);
                log.debug("BotController processed reply of {}.", "Akhmatova");
            }
            if (botName.equals("Brodskiy")) {
                sendResponseToUser(responseContainer, brodskiy);
                log.debug("BotController processed reply of {}.", "Brodskiy");
            }
            if (botName.equals("Harms")) {
                sendResponseToUser(responseContainer, harms);
                log.debug("BotController processed reply of {}.", "Harms");
            }
        }

    }

    private void sendResponseToUser(ResponseContainer responseContainer, BotsSender botsSender ) {

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
            var sendMessage = responseContainer.getSendMessage();
            sendMessage.setChatId(responseContainer.getUserId().toString());
            botsSender.sendMessage(sendMessage);
        }
        if(responseContainer.hasSticker()) {
            botsSender.sendSticker(responseContainer.getSendSticker());
        }
    }

    private void sendResponseToUserInPrivate(ResponseContainer responseContainer, TelegramWebhookCommandBot telegramWebhookBot) {
        var botsSender = (BotsSender)telegramWebhookBot;
        if (responseContainer.hasGeolocation()) {
            var sendLocation = responseContainer.getSendLocation();
            sendLocation.setChatId(responseContainer.getUserId().toString());
            botsSender.sendLocation(sendLocation);
        }
        if (responseContainer.hasAudio()) {
            var sendVoice = responseContainer.getSendVoice();
            sendVoice.setChatId(responseContainer.getUserId().toString());
            botsSender.sendVoice(sendVoice);
        }
        if (responseContainer.hasPicture()) {
            var sendPhoto = responseContainer.getSendPhoto();
            sendPhoto.setChatId(responseContainer.getUserId().toString());
            botsSender.sendPicture(sendPhoto);
        }
        if (responseContainer.hasText()) {
            if(telegramWebhookBot.isCommand(responseContainer.getSendMessage().getText())){
                var command = telegramWebhookBot.getRegisteredCommand(responseContainer.getSendMessage().getText());
                String[] arguments = new String[]{responseContainer.getMessage().getText()};
                var message = responseContainer.getMessage();
                message.setText(responseContainer.getSendMessage().getText());
                command.processMessage(telegramWebhookBot, message, arguments);
            }
            else{
                botsSender.sendMessage(responseContainer.getSendMessage());
            }
        }
        if(responseContainer.hasSticker()) {
            var sendSticker = responseContainer.getSendSticker();
            sendSticker.setChatId(responseContainer.getUserId().toString());
            botsSender.sendSticker(sendSticker);
        }
    }
}
