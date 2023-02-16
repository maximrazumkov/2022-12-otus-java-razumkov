package ru.otus.proxy;

import ru.otus.proxy.ioc.Ioc;

public class Proxy {
    public static void main(String[] args) {
        MyClassInterface myClass = Ioc.wrapClass(new MyClassImpl());
        myClass.secureAccess("Security Param");
        myClass.secureAccess("Security Param1", "Security Param2", "Security Param3");
        myClass.secureAccess("Security Param1", "Security Param2");
        myClass.secureCreate("Create secureC");
    }
}



