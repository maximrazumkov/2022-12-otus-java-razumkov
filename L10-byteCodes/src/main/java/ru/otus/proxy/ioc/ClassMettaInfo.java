package ru.otus.proxy.ioc;

import java.lang.reflect.Method;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
class ClassMettaInfo {
    final Class<?> clazz;
    final Class<?>[] interfaces;
    final Object instanceClass;
    final Set<Method> methods;
}
