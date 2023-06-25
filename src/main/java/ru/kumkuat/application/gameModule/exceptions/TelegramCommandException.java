package ru.kumkuat.application.gameModule.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TelegramCommandException extends Exception implements ClassNameExtractor{

    public TelegramCommandException() {
    }

    public TelegramCommandException(Throwable cause) {
        super(cause);
    }

    public TelegramCommandException(String message) {
        super(message);

    }
    public void getLogMessage(Object object, String message) {
        String className = extractClassName(object);
        log.info("Exception while executing command of {} " + message, className);
    }
}
