package ru.kumkuat.application.GameModule.Bot;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Data
@Slf4j
@Component
@PropertySource(name = "secret.yml", value = "secret.yml")
public class AkhmatovaBot extends TelegramWebhookBot implements BotsSender{

    @Value("${akhmatova.name}")
    private String botUsername;
    @Value("${akhmatova.token}")
    private String botToken;
    @Value("${akhmatova.path}")
    private String BotPath;

    @Override
    public BotApiMethod onWebhookUpdateReceived(Update update) {
        Message message =  update.getMessage();
        String chatId = String.valueOf(message.getChat().getId());
        int messageId = message.getMessageId();

        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Нет это не я, это кто-то другой страдает");
        sendMessage.setChatId(chatId);
        sendMessage.setReplyToMessageId(messageId);

        return sendMessage;
    }

    public void sendLocation2(SendLocation sendLocation) {

        try {
            executeAsync(sendLocation);
        } catch (TelegramApiException e) {
            e.getStackTrace();
        }
    }

    public void sendVoice(SendVoice sendVoice) {
        try {
            execute(sendVoice);
        } catch (TelegramApiException e) {
            e.getStackTrace();
        }
    }

    public void sendPicture(SendPhoto sendPhoto) {
        try {
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            e.getStackTrace();
        }
    }

    public void sendMessage(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.getStackTrace();
        }
    }
}
