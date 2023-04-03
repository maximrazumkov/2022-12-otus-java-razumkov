package ru.otus.exception;

public class NotFoundConstructorException extends RuntimeException {
    public NotFoundConstructorException(String message, Throwable cause) {
        super(message, cause);
    }
}
