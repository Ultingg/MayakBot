package ru.kumkuat.application.GameModule.Bot;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Setter
@Getter
@Component
@NoArgsConstructor
@PropertySource(name = "secret.yml", value = "secret.yml")
public class Harms extends TelegramWebhookBot implements BotsSender {

    @Value("${harms..name}")
    private String botUsername;
    @Value("${harms..token}")
    private String botToken;

    private String BotPath;

    @Override
    public BotApiMethod onWebhookUpdateReceived(Update update) {
        return new SendMessage();
    }

    public void sendLocation(SendLocation sendLocation) {
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
