package ru.kumkuat.application.GameModule.Collections;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;


@Setter
@Getter
@Component
public class ResponseContainer {

    private int timingOfReply;
    private BotApiMethod botApi;
    private SendMessage sendMessage;
    private SendLocation sendLocation;
    private SendPhoto sendPhoto;
    private SendVoice sendVoice;
    private String botName;

    public boolean hasPicture() {
        return sendPhoto != null;
    }

    public boolean hasGeolocation() {
        return sendLocation != null;
    }

    public boolean hasAudio() {
        return sendVoice != null;
    }

    public boolean hasText() {
        return sendMessage != null;
    }
}
