package com.djulbic.datafactory.metadata.providers;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class SQLExecutor {
    String connectionUrl;
    String username;
    String password;

    HikariDataSource dataSource = null;
    Connection connection = null;
    Statement statement = null;

    public SQLExecutor(String connectionUrl, String username, String password) {
        this.connectionUrl = connectionUrl;
        this.username = username;
        this.password = password;
    }

    public void start() {
        try {
            this.dataSource = getDataSource();
            this.connection = dataSource.getConnection();
            this.statement = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private HikariDataSource getDataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName(com.mysql.cj.jdbc.Driver.class.getName()); //"com.mysql.jdbc.Driver"
        dataSourceBuilder.url("jdbc:mysql://localhost:3306");//bojan?createDatabaseIfNotExist=true");
        dataSourceBuilder.username("root");
        dataSourceBuilder.password("");
        return (HikariDataSource)dataSourceBuilder.build();
    }


    public ResultSet executeAndReturnResultSet(String sqlCommand) {
        try {
            ResultSet rs = statement.executeQuery(sqlCommand);
            return rs;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void close() {
        try {
            statement.close();
            connection.close();
            dataSource.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean executeUpdate(List<String> insertQuery) {
        try {

            connection = dataSource.getConnection();
            statement = connection.createStatement();

            for (String query : insertQuery) {
                statement.executeUpdate(query);
            }

            statement.close();
            connection.close();
            dataSource.close();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }  finally {
            try {
                if (connection !=null){
                    connection.close();
                }
                if (statement !=null){
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
