package ru.kumkuat.application.gameModule.bot;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Data
@Slf4j
@Component
@PropertySource(value = "file:../resources/externalsecret.yml")
public class Brodskiy extends TelegramWebhookBot implements BotsSender {

    @Value("${brodskiy.secretName}")
    private String secretName;
    @Value("${brodskiy.name}")
    private String botUsername;
    @Value("${brodskiy.token}")
    private String botToken;
    @Value("${brodskiy.path}")
    private String BotPath;

    @Override
    public BotApiMethod onWebhookUpdateReceived(Update update) {
        return new SendMessage();
    }
    @Override
    public void sendLocation(SendLocation sendLocation) {
        log.debug("{} get SendLocationMessage!", secretName);
        try {
            execute(sendLocation);
            log.debug("{} send SendLocationMessage!", secretName);
        } catch (TelegramApiException e) {
            log.debug("{} failed sending SendLocationMessage!", secretName);
            e.getStackTrace();
        }
    }
    @Override
    public void sendVoice(SendVoice sendVoice) {
        log.debug("{} get SendVoiceMessage!", secretName);
        try {
            execute(sendVoice);
            log.debug("{} send SendVoiceMessage!", secretName);
        } catch (TelegramApiException e) {
            e.getStackTrace();
            log.debug("{} failed sending SendVoiceMessage!", secretName);
        }
    }
    @Override
    public void sendPicture(SendPhoto sendPhoto) {
        log.debug("{} get SendPhotoMessage!", secretName);
        try {
            execute(sendPhoto);
            log.debug("{} send SendPhotoMessage!", secretName);
        } catch (TelegramApiException e) {
            e.getStackTrace();
            log.debug("{} failed sending SendPhotoMessage!", secretName);
        }
    }
    @Override
    public void sendMessage(SendMessage sendMessage) {
        log.debug("{} get SendTextMessage!", secretName);
        try {
            execute(sendMessage);
            log.debug("{} send SendTextMessage!", secretName);
        } catch (TelegramApiException e) {
            e.getStackTrace();
            log.debug("{} failed sending SendTextMessage!", secretName);
        }
    }
    @Override
    public void sendSticker(SendSticker sendSticker) {
        log.debug("{} get SendTextMessage!", secretName);
        try {
            execute(sendSticker);
            log.debug("{} send SendTextMessage!", secretName);
        } catch (TelegramApiException e) {
            e.getStackTrace();
            log.debug("{} failed sending SendTextMessage!", secretName);
        }
    }

    public boolean isBotsStarting(String UserId) {
        SendMessage checkMessage = new SendMessage();
        checkMessage.setText("Проверка");
        checkMessage.setChatId(UserId);
        try {
            this.execute(checkMessage);
            return true;
        } catch (TelegramApiException e) {
            e.printStackTrace();
            return false;
        }
    }
    public String getSecretName() {
        return secretName;
    }
}
