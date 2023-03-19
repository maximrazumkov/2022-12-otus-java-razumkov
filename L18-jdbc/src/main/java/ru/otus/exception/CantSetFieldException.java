package ru.otus.exception;

public class CantSetFieldException extends RuntimeException {
    public CantSetFieldException(String message, Throwable cause) {
        super(message, cause);
    }
}
