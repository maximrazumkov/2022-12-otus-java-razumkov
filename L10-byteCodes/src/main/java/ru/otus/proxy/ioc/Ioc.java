package ru.otus.proxy.ioc;

public class Ioc {

    private Ioc() {
    }

    public static <T> T wrapClass(T instanceClass) {
        return new ProxyWrapperClass().wrap(instanceClass);
    }
}
