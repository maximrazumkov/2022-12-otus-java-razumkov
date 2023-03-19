package ru.otus.jdbc.mapper.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;
import lombok.Builder;
import ru.otus.jdbc.mapper.EntityClassMetaData;

@Builder
public class EntityClassMetaDataImpl<T> implements EntityClassMetaData<T> {

    private final String entityName;
    private final Constructor<T> constructor;
    private final Field fieldId;
    private final List<Field> allFields;
    private final List<Field> fieldsWithoutId;

    @Override
    public String getName() {
        return entityName;
    }

    @Override
    public Constructor<T> getConstructor() {
        return constructor;
    }

    @Override
    public Field getIdField() {
        return fieldId;
    }

    @Override
    public List<Field> getAllFields() {
        return allFields;
    }

    @Override
    public List<Field> getFieldsWithoutId() {
        return fieldsWithoutId;
    }
}
