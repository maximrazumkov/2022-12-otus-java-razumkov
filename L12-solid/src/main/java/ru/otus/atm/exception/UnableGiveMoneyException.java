package ru.otus.atm.exception;

public class UnableGiveMoneyException extends RuntimeException {

    public UnableGiveMoneyException(String message) {
        super(message);
    }
}
