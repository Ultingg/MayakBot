package ru.kumkuat.application.gameModule.bot;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.pinnedmessages.PinChatMessage;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.kumkuat.application.gameModule.collections.PinnedMessageDTO;

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

    public void sendPinnedMessage(PinnedMessageDTO pinnedMessageDTO) {
        try {
            long chatId;
            Message message;
            if (pinnedMessageDTO.hasMessage()) {
                SendMessage sendMessage = pinnedMessageDTO.getSendMessage();
                chatId = Long.valueOf(sendMessage.getChatId());
                message = execute(sendMessage);
            } else {
                SendPhoto sendPhoto = pinnedMessageDTO.getSendPhoto();
                chatId = Long.valueOf(sendPhoto.getChatId());
                message = execute(sendPhoto);
            }
            int messageId = message.getMessageId();
            PinChatMessage pinChatMessage = PinChatMessage.builder()
                    .chatId(chatId)
                    .disableNotification(true)
                    .messageId(messageId).build();
            execute(pinChatMessage);

        } catch (TelegramApiException e) {
            e.getStackTrace();
        }
    }


    public String getSecretName() {
        return secretName;
    }
}
