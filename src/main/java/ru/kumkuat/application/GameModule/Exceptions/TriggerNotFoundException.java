package ru.kumkuat.application.GameModule.Exceptions;

public class TriggerNotFoundException extends Exception {

    public TriggerNotFoundException() {
        super();
    }

    public TriggerNotFoundException(String message, Throwable err) {
        super(message, err);
    }

    public TriggerNotFoundException(String message) {
        super(message);
    }
}
