package ru.otus.exception;

public class CantCreateInstanceException extends RuntimeException {
    public CantCreateInstanceException(String message, Throwable cause) {
        super(message, cause);
    }
}
