package com.djulbic.datafactory;

public enum DatabaseDrivers {
    MYSQL (com.mysql.cj.jdbc.Driver.class.getName());

    private String driver;
    DatabaseDrivers(String driver){
        this.driver = driver;
    }

    public String getDriver() {
        return driver;
    }
}
