package ru.kumkuat.application.gameModule.exceptions;

public interface ClassNameExtractor {

        default String extractClassName (Object object) {
            String[] array = object.getClass().toString().split("\\.");
            String className = array[array.length - 1];
            return className;
        }
}
