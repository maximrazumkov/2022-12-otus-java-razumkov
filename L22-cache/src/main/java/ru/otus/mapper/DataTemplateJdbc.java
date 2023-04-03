package ru.otus.mapper;

import static java.util.Objects.nonNull;

import java.sql.Connection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import ru.otus.cache.HwCache;
import ru.otus.core.repository.DataTemplate;
import ru.otus.core.repository.DataTemplateException;
import ru.otus.core.repository.executor.DbExecutor;

/**
 * Сохратяет объект в базу, читает объект из базы
 */
@RequiredArgsConstructor
public class DataTemplateJdbc<T> implements DataTemplate<T> {

    private final DbExecutor dbExecutor;
    private final EntitySQLMetaData entitySQLMetaData;
    private final EntityHandler<T> entityHandler;
    private final HwCache<String, T> cache;

    @Override
    public Optional<T> findById(Connection connection, long id) {
        T result = cache.get(String.valueOf(id));
        return nonNull(result) ? Optional.of(result) : findByIdFromDb(connection, id);
    }

    @Override
    public List<T> findAll(Connection connection) {
        return dbExecutor.executeSelect(connection, entitySQLMetaData.getSelectAllSql(), Collections.emptyList(),
            entityHandler::getListResult).orElseThrow(() -> new RuntimeException("Unexpected error"));
    }

    @Override
    public long insert(Connection connection, T object) {
        try {
            long id = dbExecutor.executeStatement(connection, entitySQLMetaData.getInsertSql(),
                entityHandler.getListValuesOfFieldWithoutId(object));
            entityHandler.setId(id, object);
            cache.put(String.valueOf(id), object);
            return id;
        } catch (Exception e) {
            throw new DataTemplateException(e);
        }
    }

    @Override
    public void update(Connection connection, T object) {
        try {
            long id = dbExecutor.executeStatement(connection, entitySQLMetaData.getUpdateSql(),
                entityHandler.getListValuesOfFieldWithId(object));
            cache.put(String.valueOf(id), object);
        } catch (Exception e) {
            throw new DataTemplateException(e);
        }
    }

    private Optional<T> findByIdFromDb(Connection connection, long id) {
        Optional<T> value = dbExecutor.executeSelect(connection, entitySQLMetaData.getSelectByIdSql(),
            List.of(id),
            entityHandler::getSingleResult);
        value.ifPresent(val -> cache.put(String.valueOf(id), value.get()));
        return value;
    }
}
