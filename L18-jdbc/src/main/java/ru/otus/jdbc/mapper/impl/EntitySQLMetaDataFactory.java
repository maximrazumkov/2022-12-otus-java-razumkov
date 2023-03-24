package ru.otus.jdbc.mapper.impl;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;
import ru.otus.jdbc.mapper.EntityClassMetaData;

public class EntitySQLMetaDataFactory {

    private static final String INSERT = "insert into %s(%s) values (%s);";
    private static final String UPDATE = "update %s set %s where %s = ?;";
    private static final String SELECT_ALL = "select %s from %s;";
    private static final String SELECT_BY_ID = "select %s from %s where %s = ?;";

    private EntitySQLMetaDataFactory() {}

    public static <T> EntitySQLMetaDataImpl of(EntityClassMetaData<T> entityClassMetaData) {
        return EntitySQLMetaDataImpl.builder()
            .insert(getInsert(entityClassMetaData))
            .update(getUpdate(entityClassMetaData))
            .selectAll(getSelectAll(entityClassMetaData))
            .selectById(getSelectById(entityClassMetaData))
            .build();
    }

    private static <T> String getSelectAll(EntityClassMetaData<T> entityClassMetaData) {
        String fieldForSelect = getNamesAsStringFromFields(entityClassMetaData.getAllFields(), ", ");
        return String.format(SELECT_ALL, fieldForSelect, entityClassMetaData.getName());
    }

    public static <T> String getSelectById(EntityClassMetaData<T> entityClassMetaData) {
        String fieldForSelectById = getNamesAsStringFromFields(entityClassMetaData.getAllFields(), ", ");
        String fieldId = entityClassMetaData.getIdField().getName();
        return String.format(SELECT_BY_ID, fieldForSelectById, entityClassMetaData.getName(), fieldId);
    }

    public static <T> String getInsert(EntityClassMetaData<T> entityClassMetaData) {
        List<Field> fieldsWithoutId = entityClassMetaData.getFieldsWithoutId();
        String fieldForInsert = getNamesAsStringFromFields(fieldsWithoutId, ", ");
        String values = getInsertValues(fieldsWithoutId);
        return String.format(INSERT, entityClassMetaData.getName(), fieldForInsert, values);
    }

    public static <T> String getUpdate(EntityClassMetaData<T> entityClassMetaData) {
        String fieldForUpdate = getNamesAsStringFromFields(entityClassMetaData.getFieldsWithoutId(), " = ?, ");
        String fieldId = entityClassMetaData.getIdField().getName();
        return String.format(UPDATE, entityClassMetaData.getName(), fieldForUpdate, fieldId);
    }

    private static String getNamesAsStringFromFields(List<Field> fields, String delimiter) {
        return fields.stream()
            .map(Field::getName)
            .collect(Collectors.joining(delimiter));
    }

    private static String getInsertValues(List<Field> fieldsWithoutId) {
        return fieldsWithoutId.stream()
            .map(field -> "?")
            .collect(Collectors.joining(", "));
    }
}
