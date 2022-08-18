package ru.kumkuat.application.gameModule.marshakCommands;

import ru.kumkuat.application.gameModule.Abstract.TelegramWebhookCommandBot;

public interface IListenerSupport {

    void addListener(TelegramWebhookCommandBot telegramWebhookCommandBot);
}
