package com.djulbic.datafactory.model;

import java.util.List;

public class ExecuteRequestDTO {
    DatabaseRequestConfig config;
    List<ColumnSql> columns;

    public ExecuteRequestDTO() {

    }

    public ExecuteRequestDTO(DatabaseRequestConfig config, List<ColumnSql> columns) {
        this.config = config;
        this.columns = columns;
    }

    public DatabaseRequestConfig getConfig() {
        return config;
    }

    public void setConfig(DatabaseRequestConfig config) {
        this.config = config;
    }

    public List<ColumnSql> getColumns() {
        return columns;
    }

    public void setColumns(List<ColumnSql> columns) {
        this.columns = columns;
    }

    @Override
    public String toString() {
        return "ExecuteRequestDTO{" +
                "config=" + config +
                ", columns=" + columns +
                '}';
    }
}
