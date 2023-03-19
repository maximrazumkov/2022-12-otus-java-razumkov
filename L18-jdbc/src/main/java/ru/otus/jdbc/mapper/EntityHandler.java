package ru.otus.jdbc.mapper;

import java.sql.ResultSet;
import java.util.List;

public interface EntityHandler<T> {
    List<T> getListResult(ResultSet resultSet);
    T getSingleResult(ResultSet resultSet);
    List<Object> getListValuesOfFieldWithoutId(T object);
    List<Object> getListValuesOfFieldWithId(T object);
}
