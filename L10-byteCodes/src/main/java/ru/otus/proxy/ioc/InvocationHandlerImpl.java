package ru.otus.proxy.ioc;

import static ru.otus.proxy.util.ReflectionHelper.getFullMethodName;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.StringJoiner;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class InvocationHandlerImpl implements InvocationHandler {

    private final static String LOG_TEMPLATE = "Executed method: %s";

    final ClassMettaInfo classMettaInfo;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (classMettaInfo.getMethods().contains(method)) {
            String log = getLog(method, args);
            printLog(LOG_TEMPLATE, log);
        }
        return method.invoke(classMettaInfo.getInstanceClass(), args);
    }

    private void printLog(String logTemplate, String log) {
        System.out.println(String.format(logTemplate, log));
    }

    private String getLog(Method method, Object[] args) {
        StringJoiner stringJoiner = new StringJoiner(", ").add(method.getName());
        for (int i = 0; i < args.length; ++i) {
            String format = String.format("%s%s: %s", "param", i, args[i]);
            stringJoiner.add(format);
        }
        return stringJoiner.toString();
    }

    @Override
    public String toString() {
        return "DemoInvocationHandler{" +
            "classInfo=" + classMettaInfo.getInstanceClass() +
            '}';
    }
}
