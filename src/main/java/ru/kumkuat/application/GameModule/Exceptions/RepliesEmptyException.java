package ru.kumkuat.application.GameModule.Exceptions;

public class RepliesEmptyException extends Exception {
    public RepliesEmptyException(String message) {
        super(message);
    }

    public RepliesEmptyException() {
    }
}
