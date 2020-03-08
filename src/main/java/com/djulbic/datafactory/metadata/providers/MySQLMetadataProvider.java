package com.djulbic.datafactory.metadata.providers;

import com.djulbic.datafactory.model.ColumnSql;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.persistence.Column;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
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
        HikariDataSource dataSource = getDataSource();

        List<String> databases = new ArrayList<>();
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();

            ResultSet rs = statement.executeQuery("SHOW DATABASES");
            while(rs.next()) {
                String databaseName = rs.getString(1);
                databases.add(databaseName);
            }
            rs.close();
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            try {
                statement.close();
                connection.close();
                dataSource.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        return databases;
    }

    public List<String> getTables(String databaseName) {
        Connection connection = null;
        Statement statement = null;
        HikariDataSource dataSource = getDataSource();
        List<String> tables = new ArrayList<>();
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();

            ResultSet rs = statement.executeQuery("SHOW TABLES FROM " + databaseName);
            while(rs.next()) {
                String tableName = rs.getString(1);
                tables.add(tableName);
            }
            rs.close();
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            try {
                statement.close();
                connection.close();
                dataSource.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        return tables;
    }

    public List<ColumnSql> getColumns(String databaseName, String tableName){
        String db = String.format("`%s`.`%s`", databaseName, tableName);
        List<ColumnSql> columnSql = new ArrayList<>();

        try {
            HikariDataSource dataSource = getDataSource();

            Connection connection = dataSource.getConnection();
            DatabaseMetaData metaData = connection.getMetaData();

            // ResultSet importedKeys = metaData.getImportedKeys(null, null, tableName);
            // metaData.getPrimaryKeys(null, null, tableName);

            String databaseAndTable = databaseName + "." + tableName;
            ResultSet resultSet = metaData.getColumns(null, null, databaseAndTable, null); // ovo je db

            System.out.println("-----------------");
            PreparedStatement stmt = connection.prepareStatement("SHOW COLUMNS FROM " + db);
            ResultSet set = stmt.executeQuery();

            //store all of the columns names
            List<String> names = new ArrayList<>();
            while (set.next()) {
                String columnName = set.getString("field").toUpperCase();
                String columnType = set.getString("type").toUpperCase();
                String columnTypeLength = "";
                columnType = columnType.replaceAll("unsigned", "").trim();
                if (columnType.contains("(")){
                    int start = columnType.indexOf("(");
                    int end = columnType.indexOf(")");

                    columnTypeLength = columnType.substring(start+1,end);

                    columnType = columnType.substring(0,start);
                }

                ColumnSql column = new ColumnSql(columnName, columnType, columnTypeLength);
                columnSql.add(column);
            }
            System.out.println("-----------------");

//            while (resultSet.next()) {
//                Object object = resultSet.getObject("COLUMN_NAME");
//                System.out.println(object.getClass());
//                System.out.println(object);
//                String name = resultSet.getString("COLUMN_NAME");
//                String type = resultSet.getString("TYPE_NAME");
//                int size = resultSet.getInt("COLUMN_SIZE");
//
//                type = type.replaceAll("UNSIGNED", "").trim();
//
//                ColumnSql column = new ColumnSql(name, type, size);
//                if (!columnSql.contains(column)){
//                    columnSql.add(column);
//                }
//
//            }
            set.close();
            resultSet.close();
            connection.close();
            dataSource.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return columnSql;

    }

    public boolean insertQuery(String insertQuery){
        List<String> emptyList = new ArrayList();
        emptyList.add(insertQuery);
        return insertQuery(emptyList);
    }

    public boolean insertQuery(List<String> insertQuery){
        Connection connection = null;
        Statement statement = null;
        HikariDataSource dataSource = getDataSource();
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

    private JdbcTemplate getJdbcTemplate(){
        DataSource dataSource = getDataSource();
        JdbcTemplate template = new JdbcTemplate(dataSource);
        return template;
    }

    private HikariDataSource getDataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName(com.mysql.cj.jdbc.Driver.class.getName()); //"com.mysql.jdbc.Driver"
        dataSourceBuilder.url("jdbc:mysql://localhost:3306");//bojan?createDatabaseIfNotExist=true");
        dataSourceBuilder.username("root");
        dataSourceBuilder.password("");
        return (HikariDataSource)dataSourceBuilder.build();
    }

}
