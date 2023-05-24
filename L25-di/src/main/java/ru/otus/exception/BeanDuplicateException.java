package ru.otus.exception;

public class BeanDuplicateException extends RuntimeException {
    public BeanDuplicateException(String message) {
        super(message);
    }
}
