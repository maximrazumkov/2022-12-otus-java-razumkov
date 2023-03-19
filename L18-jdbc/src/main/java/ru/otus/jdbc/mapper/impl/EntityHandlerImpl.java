package ru.otus.jdbc.mapper.impl;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import ru.otus.core.repository.DataTemplateException;
import ru.otus.exception.CantCreateInstanceException;
import ru.otus.exception.CantGetFieldException;
import ru.otus.exception.CantSetFieldException;
import ru.otus.exception.NonUniqueResultException;
import ru.otus.jdbc.mapper.EntityClassMetaData;
import ru.otus.jdbc.mapper.EntityHandler;

@RequiredArgsConstructor
public class EntityHandlerImpl<T> implements EntityHandler<T> {

    private final static String NON_UNIQUE_RESULT = "Query method is expected to return a single result but more than one result is found";
    private final static String CANT_SET_FIELD = "Can't set field from resultSet for entity %s";
    private final static String CANT_GET_FIELD = "Can't get field from resultSet for entity %s";
    private final static String CANT_CREATE_INSTANCE = "Can't create instance for entity %s";

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
        final T instance = getInstance();
        entityClassMetaData.getAllFields().forEach(field -> setField(resultSet, instance, field));
        return instance;
    }

    private T getInstance() {
        try {
            return entityClassMetaData.getConstructor().newInstance();
        } catch (Exception e) {
            String creationInstanceError = String.format(CANT_CREATE_INSTANCE, entityClassMetaData.getName());
            throw new CantCreateInstanceException(creationInstanceError,e);
        }
    }

    private void setField(ResultSet resultSet, T instance, Field field) {
        try {
            field.setAccessible(true);
            Object object = resultSet.getObject(field.getName());
            field.set(instance, object);
        } catch (Exception e) {
            String settingFieldError = String.format(CANT_SET_FIELD, instance.getClass().getName());
            throw new CantSetFieldException(settingFieldError, e);
        }
    }

    private Object getField(Object object, String name) {
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
