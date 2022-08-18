package ru.kumkuat.application.gameModule.exceptions;

public class IncomingMessageException extends Exception {
    public IncomingMessageException() {
    }

    public IncomingMessageException(String message) {
        super(message);
    }
}
