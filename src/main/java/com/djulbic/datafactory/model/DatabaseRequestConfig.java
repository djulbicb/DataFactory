package com.djulbic.datafactory.model;

public class DatabaseRequestConfig {
    private String driver;
    private String url;
    private String username;
    private String password;
    private String databaseName;
    private String databaseTable;
    private String language;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
                "driver='" + driver + '\'' +
                ", url='" + url + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", databaseName='" + databaseName + '\'' +
                ", databaseTable='" + databaseTable + '\'' +
                ", language='" + language + '\'' +
                '}';
    }
}
