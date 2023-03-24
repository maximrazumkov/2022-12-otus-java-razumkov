package ru.otus.exception;

public class CantGetValueResultSetException extends RuntimeException {
    public CantGetValueResultSetException(String message, Throwable cause) {
        super(message, cause);
    }
}
