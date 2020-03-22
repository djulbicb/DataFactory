package com.djulbic.datafactory.model;

import java.util.List;

public class ExecuteRequestDTO {
    DatabaseRequestConfig config;
    List<ColumnSql> columns;
    int insertQount;

    public ExecuteRequestDTO() {

    }

    public ExecuteRequestDTO(DatabaseRequestConfig config, List<ColumnSql> columns, int insertQount) {
        this.config = config;
        this.columns = columns;
        this.insertQount = insertQount;
    }

    public int getInsertQount() {
        return insertQount;
    }

    public void setInsertQount(int insertQount) {
        this.insertQount = insertQount;
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
                ", insertQount=" + insertQount +
                '}';
    }
}
