package ru.kumkuat.application.GameModule.Exceptions;

public class TelegramChatServiceException extends Exception {

    public TelegramChatServiceException() {
        super();
    }

    public TelegramChatServiceException(String message, Throwable err) {
        super(message, err);
    }

    public TelegramChatServiceException(String message) {
        super(message);
    }
}
