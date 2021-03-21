package ru.kumkuat.application.GameModule.Bot;

import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.generics.BotOptions;

public interface BotsSender  extends BotOptions {
    void sendLocation(SendLocation sendLocation);


    void sendVoice(SendVoice sendVoice);

    void sendPicture(SendPhoto sendPhoto);

    void sendMessage(SendMessage sendMessage);
}
