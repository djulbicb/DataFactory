package com.djulbic.datafactory.metadata.providers;

import com.djulbic.datafactory.model.ColumnSql;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQLMetadataService {
    SQLExecutor executor = null;

    public MySQLMetadataService(String connectionUrl, String username, String password) {
        this.executor = new SQLExecutor(connectionUrl, username, password);
    }

    public List<String> getDatabases()  {
        List<String> databases = new ArrayList<>();

        executor.start();
        ResultSet resultSet = executor.executeAndReturnResultSet(SQLCommands.getShowDatabases());
        try {
            while(resultSet.next()) {
                String databaseName = resultSet.getString(1);
                databases.add(databaseName);
            }
            resultSet.close();
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            executor.close();
        }
        return databases;
    }

    public List<String> getTables(String databaseName) {
        executor.start();
        List<String> tables = new ArrayList<>();
        try {
            ResultSet results = executor.executeAndReturnResultSet(SQLCommands.getTablesByDatabaseName(databaseName));
            while(results.next()) {
                String tableName = results.getString(1);
                tables.add(tableName);
            }
            results.close();
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            executor.close();
        }
        return tables;
    }

    public void getPrimaryColumns(String tableName){
        String sqlCommand = SQLCommands.getPrimaryColumnOfTable(tableName);
        List<String> columnNames = new ArrayList<>();
    }

    /**
     * Formats string parametars into `databaseName`.`tableName`. Used for referencing objects in SQL commands
     */
    public String getJoinedDbAndTableNameAsString(String databaseName, String tableName){
        return String.format("`%s`.`%s`", databaseName, tableName);
    }

    public List<ColumnSql> getColumns(String databaseName, String tableName){
        String dbAndTableName = getJoinedDbAndTableNameAsString(databaseName, tableName);
        List<ColumnSql> columnSql = new ArrayList<>();

        try {
            executor.start();
            ResultSet set = executor.executeAndReturnResultSet(SQLCommands.getColumnsByDatabaseAndTableName(dbAndTableName));

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
            set.close();
            executor.close();
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
        executor.start();
        executor.executeUpdate(insertQuery);
        executor.close();
        return false;
    }

//    private JdbcTemplate getJdbcTemplate(){
//        DataSource dataSource = getDataSource();
//        JdbcTemplate template = new JdbcTemplate(dataSource);
//        return template;
//    }

}
