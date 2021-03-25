package ru.kumkuat.application.GameModule.Exceptions;

public class IncomingMessageException extends Exception {
    public IncomingMessageException() {
    }

    public IncomingMessageException(String message) {
        super(message);
    }
}
