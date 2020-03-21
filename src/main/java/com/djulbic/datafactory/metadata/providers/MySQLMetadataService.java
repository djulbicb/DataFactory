package com.djulbic.datafactory.metadata.providers;

import com.djulbic.datafactory.model.ColumnSql;

import java.sql.*;
import java.util.*;

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

    public Set<String> getForeignKeysColumns(String databaseName, String tableName) throws SQLException {
        HashSet<String> set = new HashSet<>();
        String sqlCommand = SQLCommands.getKeysByDbTableColumn(databaseName, tableName);
        executor.start();
        ResultSet resultSet = executor.executeAndGetResultSet(sqlCommand);
        // SELECT TABLE_SCHEMA, TABLE_NAME, COLUMN_NAME, REFERENCED_TABLE_SCHEMA, REFERENCED_TABLE_NAME, REFERENCED_COLUMN_NAME
        while (resultSet.next()){
            String table_schema = resultSet.getString("TABLE_SCHEMA");
            String table_name = resultSet.getString("TABLE_NAME");
            String column_name = resultSet.getString("COLUMN_NAME");
            String referenced_table_schema = resultSet.getString("REFERENCED_TABLE_SCHEMA");
            String referenced_table_name = resultSet.getString("REFERENCED_TABLE_NAME");
            String referenced_column_name = resultSet.getString("REFERENCED_COLUMN_NAME");

            set.add(join(table_schema, table_name, column_name));
            set.add(join(referenced_table_schema, referenced_table_name, referenced_column_name));
        }
        executor.close();

        return set;
    }

    public String join(String s1, String s2, String s3){
        return s1 + "|" + s2 + "|" + s3;
    }

    /**
     * Formats string parametars into `databaseName`.`tableName`. Used for referencing objects in SQL commands
     */
    public String getJoinedDbAndTableNameAsString(String databaseName, String tableName){
        return String.format("`%s`.`%s`", databaseName, tableName);
    }

    public List<ColumnSql> getColumns(String databaseName, String tableName) throws SQLException {
        String dbAndTableName = getJoinedDbAndTableNameAsString(databaseName, tableName);
        List<ColumnSql> columnSql = new ArrayList<>();

        List<String> primaryKeyColumns = getPrimaryColumns(databaseName, tableName);
        Set<String> foreignKeyColumns = getForeignKeysColumns(databaseName, tableName);

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

                if (primaryKeyColumns.contains(columnName.toLowerCase())){
                    column.setPrimaryKey(true);
                }

                String checkForForeighKey = join(databaseName, tableName, columnName.toLowerCase());
                if (foreignKeyColumns.contains(checkForForeighKey)){
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
