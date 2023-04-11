package ru.otus.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import ru.otus.exception.CantCreateInstanceException;
import ru.otus.exception.CantGetFieldException;
import ru.otus.exception.CantSetFieldException;


public class ReflectionUtil {

    private final static String CANT_SET_FIELD = "Can't set field from class %s";
    private final static String CANT_GET_FIELD = "Can't get field from class %s";
    private final static String CANT_CREATE_INSTANCE = "Can't create instance from constructor %s";

    private ReflectionUtil() {
    }

    public static <T> T getInstance(Constructor<T> constructor) {
        try {
            return constructor.newInstance();
        } catch (Exception e) {
            String creationInstanceError = String.format(CANT_CREATE_INSTANCE, constructor.getName());
            throw new CantCreateInstanceException(creationInstanceError,e);
        }
    }

    public static <T> void setField(Object value, T instance, Field field) {
        try {
            field.setAccessible(true);
            field.set(instance, value);
        } catch (Exception e) {
            String settingFieldError = String.format(CANT_SET_FIELD, instance.getClass().getName());
            throw new CantSetFieldException(settingFieldError, e);
        }
    }

    public static Object getField(Object object, String name) {
        try {
            var field = object.getClass().getDeclaredField(name);
            field.setAccessible(true);
            return field.get(object);
        } catch (Exception e) {
            String gettingFieldError = String.format(CANT_GET_FIELD, object.getClass().getName());
            throw new CantGetFieldException(gettingFieldError, e);
        }
    }
}
