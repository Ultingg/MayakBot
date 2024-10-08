package ru.kumkuat.application.gameModule.exceptions;

public class TelegramChatServiceException extends RuntimeException {

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
