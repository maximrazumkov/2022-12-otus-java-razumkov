package ru.otus.exception;

public class CantGetFieldException extends RuntimeException {
    public CantGetFieldException(String message, Throwable cause) {
        super(message, cause);
    }
}
