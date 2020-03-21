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
        executor.start();
        List<String> databases = executor.executeAndGetResultSetBuilder(SQLCommands.getShowDatabases()).getStringsAtIndex(1);
        executor.close();
        return databases;
    }

    public List<String> getTables(String databaseName) {
        executor.start();
        List<String> tables = executor.executeAndGetResultSetBuilder(SQLCommands.getTablesByDatabaseName(databaseName)).getStringsAtIndex(1);
        executor.close();

        return tables;
    }

    /**
     * Get primary columns of a table.
     * That information is located in result set at index 5
     * @param databaseName
     * @param tableName
     * @return
     */
    public List<String> getPrimaryColumns(String databaseName, String tableName){
        String sqlCommand = SQLCommands.getPrimaryColumnOfTable(getJoinedDbAndTableNameAsString(databaseName, tableName));
        executor.start();
        List<String> listOfPrimaryColumns = executor.executeAndGetResultSetBuilder(sqlCommand).getStringsAtIndex(5);
        executor.close();
        return listOfPrimaryColumns;

    }

    public List<String> getForeignKeysColumns(String databaseName, String tableName, String columnName){
        String sqlCommand = SQLCommands.getKeysByDbTableColumn(databaseName, tableName, columnName);
        executor.start();
        List<String> listOfForeign = executor.executeAndGetResultSetBuilder(sqlCommand).getStringsAtIndex(5);
        executor.close();
        return listOfForeign;
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

        List<String> primaryColumns = getPrimaryColumns(databaseName, tableName);
        System.out.println(">>>> " + primaryColumns);

        try {
            executor.start();
            ResultSet set = executor.executeAndGetResultSet(SQLCommands.getColumnsByDatabaseAndTableName(dbAndTableName));

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

                List<String> foreignKeysColumns = getForeignKeysColumns(databaseName, tableName, columnName);
                if (primaryColumns.contains(columnName.toLowerCase())){
                    column.setPrimaryKey(true);
                }
                if (!foreignKeysColumns.isEmpty()){
                    column.setForeignKey(true);
                }

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
