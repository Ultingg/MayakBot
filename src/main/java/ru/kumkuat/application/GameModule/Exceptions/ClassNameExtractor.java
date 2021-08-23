package ru.kumkuat.application.GameModule.Exceptions;

public interface ClassNameExtractor {

        default String extractClassName (Object object) {
            String[] array = object.getClass().toString().split("\\.");
            String className = array[array.length - 1];
            return className;
        }
}
