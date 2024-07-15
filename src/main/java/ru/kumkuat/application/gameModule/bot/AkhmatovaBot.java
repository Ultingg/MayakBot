package ru.kumkuat.application.gameModule.bot;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Data
@Slf4j
@Component
@PropertySource(value = "file:../resources/externalsecret.yml")
public class AkhmatovaBot extends TelegramWebhookBot implements BotsSender {

    @Value("${akhmatova.secretName}")
    private String secretName;
    @Value("${akhmatova.name}")
    private String botUsername;
    @Value("${akhmatova.token}")
    private String botToken;
    @Value("${akhmatova.path}")
    private String BotPath;

    @Override
    public BotApiMethod onWebhookUpdateReceived(Update update) {
        log.debug("{} get update!", secretName);
        Message message = update.getMessage();
        String chatId = String.valueOf(message.getChat().getId());
        int messageId = message.getMessageId();

        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Нет это не я, это кто-то другой страдает");
        sendMessage.setChatId(chatId);
        sendMessage.setReplyToMessageId(messageId);
        log.debug("{} sends message!", secretName);
        return sendMessage;
    }

    @Override
    public void sendLocation(SendLocation sendLocation) {
        try {
            execute(sendLocation);
        } catch (TelegramApiException e) {
            log.debug("{} failed sending SendLocationMessage!", secretName);
            e.getStackTrace();
        }
    }

    @Override
    public void sendVoice(SendVoice sendVoice) {
        try {
            execute(sendVoice);
        } catch (TelegramApiException e) {
            e.getStackTrace();
            log.debug("{} failed sending SendVoiceMessage!", secretName);
        }
    }

    @Override
    public void sendPicture(SendPhoto sendPhoto) {
        try {
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            e.getStackTrace();
            log.debug("{} failed sending SendPhotoMessage!", secretName);
        }
    }

    @Override
    public void sendMessage(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.getStackTrace();
            log.debug("{} failed sending SendTextMessage!", secretName);
        }
    }

    @Override
    public void sendSticker(SendSticker sendSticker) {
        try {
            execute(sendSticker);
        } catch (TelegramApiException e) {
            e.getStackTrace();
            log.debug("{} failed sending SendTextMessage!", secretName);
        }
    }

    public String getSecretName() {
        return secretName;
    }
}
