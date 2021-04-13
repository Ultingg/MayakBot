package ru.kumkuat.application.GameModule.Bot;

import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.generics.BotOptions;

public interface BotsSender extends BotOptions {
    void sendLocation(SendLocation sendLocation);

    void sendVoice(SendVoice sendVoice);

    void sendPicture(SendPhoto sendPhoto);

    void sendMessage(SendMessage sendMessage);

    void sendSticker(SendSticker sendSticker);
}
