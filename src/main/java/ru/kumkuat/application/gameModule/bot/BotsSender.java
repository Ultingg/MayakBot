package ru.kumkuat.application.gameModule.bot;

import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.generics.BotOptions;
import ru.kumkuat.application.gameModule.collections.PinnedMessageDTO;

public interface BotsSender extends BotOptions {

    String getSecretName();

    void sendLocation(SendLocation sendLocation);

    void sendVoice(SendVoice sendVoice);

    void sendPicture(SendPhoto sendPhoto);

    void sendMessage(SendMessage sendMessage);

    void sendSticker(SendSticker sendSticker);

    void sendPinnedMessage(PinnedMessageDTO pinnedMessageDTO);
}
