package ru.otus.exception;

public class BeanAlreadyExistException extends RuntimeException {
    public BeanAlreadyExistException(String message) {
        super(message);
    }
}
