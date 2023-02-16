package ru.otus.proxy.ioc;

class NotFoundInterfaceException extends RuntimeException {
    public NotFoundInterfaceException(String message) {
        super(message);
    }
}
