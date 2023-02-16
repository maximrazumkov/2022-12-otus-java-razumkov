package ru.otus.proxy.ioc;

import static ru.otus.proxy.util.ReflectionHelper.instantiate;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import ru.otus.proxy.ioc.annotation.Log;
import ru.otus.proxy.util.ReflectionHelper;

class ProxyWrapperClass implements WrapperClass {

    static final String NOT_FOUND_INTERFACE = "Class '%s' doesn't have any interfaces";

    @Override
    public <T> T wrap(T instanceClass) {
        ClassMettaInfo classMettaInfo = parseClass(instanceClass);
        validate(classMettaInfo);
        InvocationHandler handler = new InvocationHandlerImpl(classMettaInfo);
        return (T) Proxy.newProxyInstance(ProxyWrapperClass.class.getClassLoader(),
            classMettaInfo.getInterfaces(), handler);
    }

    private void validate(ClassMettaInfo classMettaInfo) {
        if (classMettaInfo.getInterfaces().length == 0) {
            String errorMessage = String.format(NOT_FOUND_INTERFACE,
                classMettaInfo.getClazz().getCanonicalName());
            throw new NotFoundInterfaceException(errorMessage);
        }
    }

    private <T> ClassMettaInfo parseClass(T instanceClass) {
        Class<?> clazz = instanceClass.getClass();
        Object instance = instantiate(clazz);
        Class<?>[] interfaces = clazz.getInterfaces();
        Set<String> methods = getMethodsNamesByAnnotation(clazz.getDeclaredMethods(), Log.class);
        Set<Method> declaredMethods = getDeclaredMethods(interfaces, methods);
        return buildClassMettaInfo(clazz, instance, interfaces, declaredMethods);
    }

    private Set<Method> getDeclaredMethods(Class<?>[] interfaces, Set<String> classMethods) {
        return Arrays.stream(interfaces)
            .map(Class::getDeclaredMethods)
            .flatMap(Arrays::stream)
            .filter(method -> classMethods.contains(ReflectionHelper.getFullMethodName(method)))
            .collect(Collectors.toSet());
    }

    private ClassMettaInfo buildClassMettaInfo(
        Class<?> clazz, Object instance,
        Class<?>[] interfaces, Set<Method> methods
    ) {
        return ClassMettaInfo.builder()
            .clazz(clazz)
            .instanceClass(instance)
            .interfaces(interfaces)
            .methods(methods)
            .build();
    }

    private Set<String> getMethodsNamesByAnnotation(
        Method[] declaredMethods,
        Class<? extends Annotation> annotation
    ) {
        return Arrays.stream(declaredMethods)
            .filter(method -> method.isAnnotationPresent(annotation))
            .map(ReflectionHelper::getFullMethodName)
            .collect(Collectors.toSet());
    }
}
