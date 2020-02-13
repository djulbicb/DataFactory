package com.djulbic.datafactory.metadata.providers;

import com.djulbic.datafactory.model.ColumnSql;
import org.springframework.boot.jdbc.DataSourceBuilder;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQLMetadataProvider {

    private String connectionUrl;
    private String username;
    private String password;

    public MySQLMetadataProvider(String connectionUrl, String username, String password) {
        this.connectionUrl = connectionUrl;
        this.username = username;
        this.password = password;
    }

    public List<String> getDatabases()  {
        Connection connection = null;
        Statement statement = null;
        List<String> databases = new ArrayList<>();
        try {
            connection = getDataSource().getConnection();
            statement = connection.createStatement();

            ResultSet rs = statement.executeQuery("SHOW DATABASES");
            while(rs.next()) {
                String databaseName = rs.getString(1);
                databases.add(databaseName);
            }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            try {
                statement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        return databases;
    }

    public List<String> getTables(String databaseName) {
        Connection connection = null;
        Statement statement = null;
        List<String> tables = new ArrayList<>();
        try {
            connection = getDataSource().getConnection();
            statement = connection.createStatement();

            ResultSet rs = statement.executeQuery("SHOW TABLES FROM " + databaseName);
            while(rs.next()) {
                String tableName = rs.getString(1);
                tables.add(tableName);
            }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            try {
                statement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        return tables;
    }

    public List<ColumnSql> getColumns(String databaseName, String tableName){
        List<ColumnSql> columnSql = new ArrayList<>();

        try {
            Connection connection = getDataSource().getConnection();
            DatabaseMetaData metaData = connection.getMetaData();

            String databaseAndTable = databaseName + "." + tableName;
            ResultSet resultSet = metaData.getColumns(null, null, databaseAndTable, null); // ovo je db

            while (resultSet.next()) {
                String name = resultSet.getString("COLUMN_NAME");
                String type = resultSet.getString("TYPE_NAME");
                int size = resultSet.getInt("COLUMN_SIZE");

                ColumnSql column = new ColumnSql(name, type, size);
                // System.out.println("Column name: [" + name + "];" + "type: [" + type + "]; size: [" + size + "]"); // Column name: [id]; type: [INT]; size: [10]

                columnSql.add(column);
            }

            connection.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return columnSql;

    }

    private DataSource getDataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName(com.mysql.cj.jdbc.Driver.class.getName()); //"com.mysql.jdbc.Driver"
        dataSourceBuilder.url("jdbc:mysql://localhost:3306");//bojan?createDatabaseIfNotExist=true");
        dataSourceBuilder.username("root");
        dataSourceBuilder.password("");
        return dataSourceBuilder.build();
    }

}
