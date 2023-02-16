package ru.otus.proxy;

import ru.otus.proxy.ioc.annotation.Log;

public class MyClassImpl implements MyClassInterface {

    @Log
    public void secureAccess(String param) {
        System.out.println("secureAccess, param:" + param);
    }

    @Override
    public void secureAccess(String param1, String param2) {
        System.out.println("secureAccess, param1:" + param1);
        System.out.println("secureAccess, param2:" + param2);
    }

    @Log
    @Override
    public void secureAccess(String param1, String param2, String param3) {
        System.out.println("secureAccess, param1:" + param1);
        System.out.println("secureAccess, param2:" + param2);
        System.out.println("secureAccess, param3:" + param3);
    }

    @Log
    @Override
    public void secureCreate(String param) {
        System.out.println("secureAccess, param:" + param);
    }

    @Override
    public String toString() {
        return "MyClassImpl{}";
    }
}
