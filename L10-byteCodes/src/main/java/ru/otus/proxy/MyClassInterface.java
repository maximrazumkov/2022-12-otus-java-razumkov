package ru.otus.proxy;

import ru.otus.proxy.ioc.annotation.Log;

public interface MyClassInterface {
    void secureAccess(String param);
    void secureAccess(String param1, String param2);
    void secureAccess(String param1, String param2, String param3);
    void secureCreate(String param);
}
