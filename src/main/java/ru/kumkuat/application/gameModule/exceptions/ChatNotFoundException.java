package ru.kumkuat.application.gameModule.exceptions;

public class ChatNotFoundException extends RuntimeException {
    public ChatNotFoundException(String message) {
        super(message);
    }
}
