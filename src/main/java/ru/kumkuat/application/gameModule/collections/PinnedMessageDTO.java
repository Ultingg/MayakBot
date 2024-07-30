package ru.kumkuat.application.gameModule.collections;

import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;

@Getter
@Setter
public class PinnedMessageDTO {
    private SendMessage sendMessage;
    private SendPhoto sendPhoto;

    public boolean hasPhoto() {
        return this.sendPhoto != null;
    }

    public boolean hasMessage() {
        return this.sendMessage != null;
    }
}
