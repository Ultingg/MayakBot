package ru.kumkuat.application.GameModule.Exceptions;

public class TriggerEmptyException extends Exception {
    public TriggerEmptyException() {
    }

    public TriggerEmptyException(String message) {
        super(message);
    }
}
