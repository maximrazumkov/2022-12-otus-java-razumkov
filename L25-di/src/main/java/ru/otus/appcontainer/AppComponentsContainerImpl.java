package ru.otus.appcontainer;

import java.lang.reflect.Method;
import java.util.stream.Collectors;
import org.reflections.Reflections;
import ru.otus.exception.BeanAlreadyExistException;
import ru.otus.exception.BeanDuplicateException;
import ru.otus.exception.BeanNotFoundException;
import ru.otus.appcontainer.api.AppComponent;
import ru.otus.appcontainer.api.AppComponentsContainer;
import ru.otus.appcontainer.api.AppComponentsContainerConfig;

import static java.util.Objects.nonNull;
import static org.reflections.ReflectionUtils.*;
import static ru.otus.util.ReflectionHelper.callMethod;
import static ru.otus.util.ReflectionHelper.instantiate;

import java.util.*;

public class AppComponentsContainerImpl implements AppComponentsContainer {

    private final static String BEAN_NOT_FOUND_ERROR = "Bean %s not found in context.";
    private final static String BEAN_DUPLICATE_FOUND_ERROR = "Bean %s duplicate in context.";
    private final static String BEAN_ALREADY_EXIST_ERROR = "Bean %s already exist.";
    private final static String ILLEGAL_ARGUMENT_ERROR = "Given class is not config %s";

    private final List<Object> appComponents = new ArrayList<>();
    private final Map<String, Object> appComponentsByName = new HashMap<>();

    public AppComponentsContainerImpl(Class<?> initialConfigClass) {
        processConfig(initialConfigClass);
    }

    public AppComponentsContainerImpl(String pathConfig) {
        processConfig(pathConfig);
    }

    private int compareClassesByAppComponentsContainerConfig(Class<?> c1, Class<?> c2) {
        return c1.getAnnotation(AppComponentsContainerConfig.class).order()
            - c2.getAnnotation(AppComponentsContainerConfig.class).order();
    }

    private void processConfig(String pathConfig) {
        Reflections reflections = new Reflections(pathConfig);
        TreeSet<Class<?>> classTreeSet = new TreeSet<>(this::compareClassesByAppComponentsContainerConfig);
        Set<Class<?>> classSet = reflections.getTypesAnnotatedWith(AppComponentsContainerConfig.class);
        classTreeSet.addAll(classSet);
        classTreeSet.forEach(this::processConfig);
    }

    private void processConfig(Class<?> configClass) {
        checkConfigClass(configClass);
        Object instantiateOfConfigClass = instantiate(configClass);
        List<Method> methodList = getMethodsOfClassByAppComponent(configClass);
        methodList.forEach(method -> fillBeanToContext(instantiateOfConfigClass, method));
    }

    private void fillBeanToContext(Object configClass, Method method) {
        Object[] parameters = getMethodParameters(method);
        String beanName = method.getAnnotation(AppComponent.class).name();
        Object bean = callMethod(configClass, method, parameters);
        validateBean(beanName);
        appComponentsByName.put(beanName, bean);
        appComponents.add(bean);
    }

    private void validateBean(String beanName) {
        if (nonNull(appComponentsByName.get(beanName))) {
            throw new BeanAlreadyExistException(String.format(BEAN_ALREADY_EXIST_ERROR, beanName));
        }
    }

    private Object[] getMethodParameters(Method method) {
        return Arrays.stream(method.getParameters())
            .map(parameter -> getAppComponent(parameter.getType()))
            .toArray();
    }

    private List<Method> getMethodsOfClassByAppComponent(Class<?> configClass) {
        return get(Methods.of(configClass)).stream()
            .filter(method -> method.isAnnotationPresent(AppComponent.class))
            .sorted(Comparator.comparingInt(m -> m.getAnnotation(AppComponent.class).order()))
            .collect(Collectors.toList());
    }

    private void checkConfigClass(Class<?> configClass) {
        if (!configClass.isAnnotationPresent(AppComponentsContainerConfig.class)) {
            throw new IllegalArgumentException(String.format(ILLEGAL_ARGUMENT_ERROR, configClass.getName()));
        }
    }

    @Override
    public <C> C getAppComponent(Class<C> componentClass) {
        List<Object> componentByClassList = getComponentByClass(componentClass);
        validateAppComponent(componentByClassList, componentClass);
        return (C) componentByClassList.get(0);
    }

    private void validateAppComponent(List<Object> componentByClassList, Class<?> componentClass) {
        if (componentByClassList.isEmpty()) {
            throw new BeanNotFoundException(String.format(BEAN_NOT_FOUND_ERROR, componentClass));
        }
        if (componentByClassList.size() > 1) {
            throw new BeanDuplicateException(String.format(BEAN_DUPLICATE_FOUND_ERROR, componentClass));
        }
    }

    private List<Object> getComponentByClass(Class<?> componentClass) {
        return appComponents.stream()
            .filter(bean -> checkBean(bean, componentClass))
            .collect(Collectors.toList());
    }

    private <C> boolean checkBean(Object bean, Class<C> componentClass) {
        boolean assignableFrom = componentClass.isAssignableFrom(bean.getClass());
        return assignableFrom;
    }

    @Override
    public <C> C getAppComponent(String componentName) {
        return (C) Optional.ofNullable(appComponentsByName.get(componentName))
            .orElseThrow(() -> new BeanNotFoundException(String.format(BEAN_NOT_FOUND_ERROR, componentName)));
    }
}
