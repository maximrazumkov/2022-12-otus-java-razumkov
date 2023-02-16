package ru.otus.proxy.ioc;

interface WrapperClass {
    <T> T wrap(T instanceClass);
}
