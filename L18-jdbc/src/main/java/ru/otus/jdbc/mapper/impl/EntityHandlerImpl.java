package ru.otus.jdbc.mapper.impl;

import static ru.otus.util.ReflectionUtil.getField;
import static ru.otus.util.ReflectionUtil.setField;
import static ru.otus.util.ReflectionUtil.getInstance;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import ru.otus.core.repository.DataTemplateException;
import ru.otus.exception.CantGetValueResultSetException;
import ru.otus.exception.NonUniqueResultException;
import ru.otus.jdbc.mapper.EntityClassMetaData;
import ru.otus.jdbc.mapper.EntityHandler;

@RequiredArgsConstructor
public class EntityHandlerImpl<T> implements EntityHandler<T> {

    private final static String NON_UNIQUE_RESULT = "Query method is expected to return a single result but more than one result is found";
    private final static String CANT_GET_VALUE_FROM_RESULT_SET = "Cant get value from resultSet of field %s";

    private final EntityClassMetaData<T> entityClassMetaData;

    @Override
    public List<T> getListResult(ResultSet resultSet) {
        try {
            return fillListResultFromResultSet(resultSet);
        } catch (SQLException e) {
            throw new DataTemplateException(e);
        }
    }

    private ArrayList<T> fillListResultFromResultSet(ResultSet resultSet) throws SQLException {
        var resultList = new ArrayList<T>();
        while (resultSet.next()) {
            resultList.add(getResult(resultSet));
        }
        return resultList;
    }

    @Override
    public T getSingleResult(ResultSet resultSet) {
        try {
            return fillSingleResultFromResultSet(resultSet);
        } catch (SQLException e) {
            throw new DataTemplateException(e);
        }
    }

    private T fillSingleResultFromResultSet(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            T result = getResult(resultSet);
            validateSingleResult(resultSet);
            return result;
        }
        return null;
    }

    private void validateSingleResult(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            throw new NonUniqueResultException(NON_UNIQUE_RESULT);
        }
    }

    @Override
    public List<Object> getListValuesOfFieldWithoutId(T object) {
        return entityClassMetaData.getFieldsWithoutId().stream()
            .map(field -> getField(object, field.getName()))
            .toList();
    }

    @Override
    public List<Object> getListValuesOfFieldWithId(T object) {
        List<Object> listValuesOfFieldWithoutId = new ArrayList<>(getListValuesOfFieldWithoutId(object));
        String fieldIdName = entityClassMetaData.getIdField().getName();
        Object fieldId = getField(object, fieldIdName);
        listValuesOfFieldWithoutId.add(fieldId);
        return listValuesOfFieldWithoutId.stream().toList();
    }

    private T getResult(ResultSet resultSet) {
        final T instance = getInstance(entityClassMetaData.getConstructor());
        entityClassMetaData.getAllFields().forEach(field -> setField(
            getValueFromResultSet(resultSet, field.getName()), instance, field));
        return instance;
    }

    private Object getValueFromResultSet(ResultSet resultSet, String fieldName) {
        try {
            return resultSet.getObject(fieldName);
        } catch (Exception e) {
            String gettingValueErr = String.format(CANT_GET_VALUE_FROM_RESULT_SET, fieldName);
            throw new CantGetValueResultSetException(gettingValueErr, e);
        }
    }
}
