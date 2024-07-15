package ru.kumkuat.application.gameModule.service.update.processors;

import org.telegram.telegrambots.meta.api.objects.Message;

public interface UpdateProcessor {

    void processUpdate(Message updateMessage);
}
