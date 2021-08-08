package ru.kumkuat.application.GameModule.Commands.MarshakCommands;

import ru.kumkuat.application.GameModule.Abstract.TelegramWebhookCommandBot;

public interface IListenerSupport {

    void addListener(TelegramWebhookCommandBot telegramWebhookCommandBot);
}
