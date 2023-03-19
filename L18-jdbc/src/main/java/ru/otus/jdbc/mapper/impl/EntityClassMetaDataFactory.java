package ru.otus.jdbc.mapper.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import ru.otus.annotation.Id;
import ru.otus.exception.NotFoundConstructorException;
import ru.otus.exception.NotFoundEntityIdException;

public class EntityClassMetaDataFactory {

    private static final String NOT_FOUND_ID = "Not found id in entity %s";
    private static final String NOT_FOUND_CONSTRUCTOR = "Not found constructor in entity %s";

    public static <T> EntityClassMetaDataImpl<T> of(Class<T> clazz) {
        String entityName = clazz.getSimpleName().toLowerCase(Locale.ROOT);
        Constructor<T> constructor = getConstructor(clazz);
        List<Field> fields = Arrays.stream(clazz.getDeclaredFields()).collect(Collectors.toList());
        Field fieldId = getFieldIdByAnnotation(entityName, fields, Id.class);
        List<Field> fieldsWithoutId = getFieldsWithoutAnnotation(fields, Id.class);
        return buildEntityClassMetaData(entityName, constructor, fieldId, fields, fieldsWithoutId);
    }

    private static <T> EntityClassMetaDataImpl<T> buildEntityClassMetaData(
        String entityName, Constructor<T> constructor, Field fieldId,
        List<Field> allFields, List<Field> fieldsWithoutId
    ) {
        return EntityClassMetaDataImpl.<T>builder()
            .entityName(entityName)
            .constructor(constructor)
            .fieldId(fieldId)
            .allFields(allFields)
            .fieldsWithoutId(fieldsWithoutId)
            .build();
    }

    private static <T> Constructor<T> getConstructor(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor();
        } catch (Exception e) {
            String notFoundConstructor = String.format(NOT_FOUND_CONSTRUCTOR, clazz.getName());
            throw new NotFoundConstructorException(notFoundConstructor, e);
        }
    }

    private static Field getFieldIdByAnnotation(String className, List<Field> fields, Class<? extends Annotation> annotation) {
        List<Field> fieldsWithAnnotation = getFieldsWithAnnotation(fields, annotation);
        validateId(className, fieldsWithAnnotation);
        return fieldsWithAnnotation.get(0);
    }

    private static void validateId(String className, List<Field> fieldsWithAnnotation) {
        if (fieldsWithAnnotation.size() != 1) {
            String notFoundId = String.format(NOT_FOUND_ID, className);
            throw new NotFoundEntityIdException(notFoundId);
        }
    }

    private static List<Field> getFieldsWithAnnotation(List<Field> fields, Class<? extends Annotation> annotation) {
        return fields.stream()
            .filter(field -> field.isAnnotationPresent(annotation))
            .toList();
    }

    private static List<Field> getFieldsWithoutAnnotation(List<Field> fields, Class<? extends Annotation> annotation) {
        return fields.stream()
            .filter(field -> !field.isAnnotationPresent(annotation))
            .toList();
    }
}
