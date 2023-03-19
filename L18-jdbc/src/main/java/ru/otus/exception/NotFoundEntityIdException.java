package ru.otus.exception;

public class NotFoundEntityIdException extends RuntimeException {
    public NotFoundEntityIdException(String message) {
        super(message);
    }
}
