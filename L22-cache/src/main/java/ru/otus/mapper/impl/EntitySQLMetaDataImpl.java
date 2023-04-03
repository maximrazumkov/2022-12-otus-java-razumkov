package ru.otus.mapper.impl;

import lombok.Builder;
import ru.otus.mapper.EntitySQLMetaData;

@Builder
class EntitySQLMetaDataImpl implements EntitySQLMetaData {

    private final String insert;
    private final String update;
    private final String selectAll;
    private final String selectById;

    @Override
    public String getSelectAllSql() {
        return selectAll;
    }

    @Override
    public String getSelectByIdSql() {
        return selectById;
    }

    @Override
    public String getInsertSql() {
        return insert;
    }

    @Override
    public String getUpdateSql() {
        return update;
    }
}
