package com.djulbic.datafactory.model;

public class DatabaseRequestConfig extends DbConnection {
    private String databaseName;
    private String databaseTable;
    private String language;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getDatabaseTable() {
        return databaseTable;
    }

    public void setDatabaseTable(String databaseTable) {
        this.databaseTable = databaseTable;
    }

    @Override
    public String toString() {
        return "DatabaseRequestConfig{" +
                "driver='" + getDriver() + '\'' +
                ", url='" + getUrl() + '\'' +
                ", username='" + getUsername() + '\'' +
                ", password='" + getPassword() + '\'' +
                ", databaseName='" + databaseName + '\'' +
                ", databaseTable='" + databaseTable + '\'' +
                ", language='" + language + '\'' +
                '}';
    }
}
