package com.djulbic.datafactory.metadata.providers;

import com.djulbic.datafactory.DatabaseDrivers;
import com.djulbic.datafactory.model.DbConnection;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class SQLExecutor {

    HikariDataSource dataSource = null;
    Connection connection = null;
    Statement statement = null;

    DbConnection databaseConfig = null;

    public SQLExecutor(DbConnection databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    public void start() throws SQLException {
        this.dataSource = getDataSource();
        this.connection = dataSource.getConnection();
        this.statement = connection.createStatement();
    }

    private HikariDataSource getDataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        String driverClassName = DatabaseDrivers.valueOf(this.databaseConfig.getDriver()).getDriver();
        dataSourceBuilder.driverClassName(driverClassName); //"com.mysql.jdbc.Driver"
        //dataSourceBuilder.driverClassName(com.mysql.cj.jdbc.Driver.class.getName()); //"com.mysql.jdbc.Driver"
        dataSourceBuilder.url(this.databaseConfig.getUrl());//bojan?createDatabaseIfNotExist=true");
        dataSourceBuilder.username(this.databaseConfig.getUsername());
        dataSourceBuilder.password(this.databaseConfig.getPassword());


//        dataSourceBuilder.driverClassName(com.mysql.cj.jdbc.Driver.class.getName()); //"com.mysql.jdbc.Driver"
//        dataSourceBuilder.url("jdbc:mysql://localhost:3306");//bojan?createDatabaseIfNotExist=true");
//        dataSourceBuilder.username("root");
//        dataSourceBuilder.password("");
        return (HikariDataSource)dataSourceBuilder.build();
    }

    public ResultSet executeAndGetResultSet(String sqlCommand) throws SQLException {
        return statement.executeQuery(sqlCommand);
    }

    public ResultSetBuilder executeAndGetResultSetBuilder(String sqlCommand) throws SQLException {
        ResultSet rs = statement.executeQuery(sqlCommand);
        return new ResultSetBuilder(rs);
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

    public boolean executeUpdate(List<String> insertQuery) throws SQLException {
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            for (String query : insertQuery) {
                statement.executeUpdate(query);
            }
            return true;
        } catch (SQLException e) {
           throw e;
        }  finally {
            try {
                statement.close();
                connection.close();
                dataSource.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
