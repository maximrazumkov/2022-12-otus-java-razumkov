package ru.otus.jdbc.mapper.impl;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import ru.otus.jdbc.mapper.EntityClassMetaData;
import ru.otus.jdbc.mapper.EntitySQLMetaData;

@RequiredArgsConstructor
public class EntitySQLMetaDataImpl<T> implements EntitySQLMetaData {

    private static final String INSERT = "insert into %s(%s) values (%s);";
    private static final String UPDATE = "update %s set %s where %s = ?;";
    private static final String SELECT_ALL = "select %s from %s;";
    private static final String SELECT_BY_ID = "select %s from %s where %s = ?;";

    private final EntityClassMetaData<T> entityClassMetaData;

    @Override
    public String getSelectAllSql() {
        String fieldForSelect = getNamesAsStringFromFields(entityClassMetaData.getAllFields(), ", ");
        return String.format(SELECT_ALL, fieldForSelect, entityClassMetaData.getName());
    }

    @Override
    public String getSelectByIdSql() {
        String fieldForSelectById = getNamesAsStringFromFields(entityClassMetaData.getAllFields(), ", ");
        String fieldId = entityClassMetaData.getIdField().getName();
        return String.format(SELECT_BY_ID, fieldForSelectById, entityClassMetaData.getName(), fieldId);
    }

    @Override
    public String getInsertSql() {
        List<Field> fieldsWithoutId = entityClassMetaData.getFieldsWithoutId();
        String fieldForInsert = getNamesAsStringFromFields(fieldsWithoutId, ", ");
        String values = getInsertValues(fieldsWithoutId);
        return String.format(INSERT, entityClassMetaData.getName(), fieldForInsert, values);
    }

    private String getInsertValues(List<Field> fieldsWithoutId) {
        return fieldsWithoutId.stream()
            .map(field -> "?")
            .collect(Collectors.joining(", "));
    }

    @Override
    public String getUpdateSql() {
        String fieldForUpdate = getNamesAsStringFromFields(entityClassMetaData.getFieldsWithoutId(), " = ?, ");
        String fieldId = entityClassMetaData.getIdField().getName();
        return String.format(UPDATE, entityClassMetaData.getName(), fieldForUpdate, fieldId);
    }

    private String getNamesAsStringFromFields(List<Field> fields, String delimiter) {
        return fields.stream()
            .map(Field::getName)
            .collect(Collectors.joining(delimiter));
    }
}
