package ru.kumkuat.application.gameModule.bot;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;

@Data
@Slf4j
@Component
@PropertySource(value = "file:../resources/externalsecret.yml")
public class Brodskiy extends TelegramWebhookBot implements BotsSender {
    private final static Logger logger = LoggerFactory.getLogger(Brodskiy.class);

    @Value("${brodskiy.secretName}")
    private String secretName;
    @Value("${brodskiy.name}")
    private String botUsername;
    @Value("${brodskiy.token}")
    private String botToken;
    @Value("${brodskiy.path}")
    private String BotPath;

    @PostConstruct
    void settingUp() {
        try {
            SetWebhook setWebhook = new SetWebhook();
            setWebhook.setUrl(this.getBotPath());
            this.execute(setWebhook);
            logger.info("Brodskiy Post construct finished");
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public BotApiMethod onWebhookUpdateReceived(Update update) {
        return new SendMessage();
    }

    @Override
    public void sendLocation(SendLocation sendLocation) {
        try {
            executeAsync(sendLocation);
        } catch (TelegramApiException e) {
            log.error("{} failed sending SendLocationMessage!", secretName);
            e.getStackTrace();
        }
    }

    @Override
    public void sendVoice(SendVoice sendVoice) {
        try {
            executeAsync(sendVoice);
        } catch (Exception e) {
            e.getStackTrace();
            log.debug("{} failed sending SendVoiceMessage!", secretName);
        }
    }

    @Override
    public void sendPicture(SendPhoto sendPhoto) {
        try {
            executeAsync(sendPhoto);
        } catch (Exception e) {
            e.getStackTrace();
            log.debug("{} failed sending SendPhotoMessage!", secretName);
        }
    }

    @Override
    public void sendMessage(SendMessage sendMessage) {
        try {
            executeAsync(sendMessage);
        } catch (TelegramApiException e) {
            e.getStackTrace();
            log.debug("{} failed sending SendTextMessage!", secretName);
        }
    }

    @Override
    public void sendSticker(SendSticker sendSticker) {
        try {
            executeAsync(sendSticker);
        } catch (Exception e) {
            e.getStackTrace();
            log.debug("{} failed sending SendTextMessage!", secretName);
        }
    }

    public String getSecretName() {
        return secretName;
    }
}
