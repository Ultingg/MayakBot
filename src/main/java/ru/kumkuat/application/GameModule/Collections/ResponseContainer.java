package ru.kumkuat.application.GameModule.Collections;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.objects.Message;


@Setter
@Getter
@Component
public class ResponseContainer {

    private int timingOfReply;
    private Long userId;
    private BotApiMethod botApi;
    private SendMessage sendMessage;
    private SendLocation sendLocation;
    private SendPhoto sendPhoto;
    private SendVoice sendVoice;
    private SendSticker sendSticker;
    private String botName;
    private Message message;
    private boolean wrongMessage;

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

    public boolean hasSticker() {
        return sendSticker != null;
    }
}
