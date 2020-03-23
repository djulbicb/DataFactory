package com.djulbic.datafactory.metadata.providers;

import com.djulbic.datafactory.model.ColumnSql;
import com.djulbic.datafactory.model.DatabaseRequestConfig;
import com.djulbic.datafactory.model.DbConnection;
import com.djulbic.datafactory.model.ExecuteRequestDTO;

import java.sql.*;
import java.util.*;

public class MySQLMetadataService {

    DatabaseRequestConfig request;

    public MySQLMetadataService(DatabaseRequestConfig request) {
        this.request = request;
    }

    public List<String> getDatabases() throws SQLException {
        SQLExecutor executor = new SQLExecutor(request);

        executor.start();
        List<String> databases =
                executor.executeAndGetResultSetBuilder(SQLCommands.getShowDatabases()).getStringsAtIndex(1);
        executor.close();

        return databases;
    }

    public List<String> getTables() throws SQLException {
        SQLExecutor executor = new SQLExecutor(request);
        String databaseName = request.getDatabaseName();
        executor.start();
        List<String> tables = executor.executeAndGetResultSetBuilder(SQLCommands.getTablesByDatabaseName(databaseName)).getStringsAtIndex(1);
        executor.close();

        return tables;
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

    public List<ColumnSql> getColumns() throws SQLException {
        SQLExecutor executor = new SQLExecutor(request);

        String databaseName = request.getDatabaseName();
        String tableName = request.getDatabaseTable();
        String dbAndTableName = getJoinedDbAndTableNameAsString(databaseName, tableName);
        List<ColumnSql> columnSql = new ArrayList<>();

        ColumnMetadata columnMetadata = getColumnMetadata();

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

                if (columnMetadata.getPrimaryKeyColumns().contains(columnName.toLowerCase())){
                    column.setPrimaryKey(true);
                }

                String checkForForeighKey = join(databaseName, tableName, columnName.toLowerCase());
                if (columnMetadata.getForeignKeyColumns().contains(checkForForeighKey)){
                    column.setForeignKey(true);
                }

                if (columnMetadata.getNullableCollumns().contains(columnName.toLowerCase())){
                    column.setNullable(true);
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


    public boolean insertQuery(String insertQuery) throws SQLException {
        List<String> emptyList = new ArrayList();
        emptyList.add(insertQuery);
        return insertQuery(emptyList);
    }

    public boolean insertQuery(List<String> insertQuery) throws SQLException {
        SQLExecutor executor = new SQLExecutor(request);
        executor.start();
        executor.executeUpdate(insertQuery);
        executor.close();
        return false;
    }

    private ColumnMetadata getColumnMetadata() throws SQLException {
        SQLExecutor executor = new SQLExecutor(request);
        executor.start();
        ColumnMetadata metadata = new ColumnMetadata( getPrimaryColumns(executor),  getForeignKeysColumns(executor), getNullableColumns(executor)) ;
        executor.close();
        return metadata;
    }

//    private JdbcTemplate getJdbcTemplate(){
//        DataSource dataSource = getDataSource();
//        JdbcTemplate template = new JdbcTemplate(dataSource);
//        return template;
//    }

    /**
     * Get primary columns of a table.
     * That information is located in result set at index 5
     * @return
     */
    public List<String> getPrimaryColumns(SQLExecutor executor) throws SQLException {
        String databaseName = request.getDatabaseName();
        String tableName = request.getDatabaseTable();

        String sqlCommand = SQLCommands.getPrimaryColumnOfTable(getJoinedDbAndTableNameAsString(databaseName, tableName));
        List<String> listOfPrimaryColumns = executor.executeAndGetResultSetBuilder(sqlCommand).getStringsAtIndex(5);

        return listOfPrimaryColumns;
    }

    public Set<String> getForeignKeysColumns(SQLExecutor executor) throws SQLException {
        HashSet<String> set = new HashSet<>();
        String databaseName = request.getDatabaseName();
        String tableName = request.getDatabaseTable();

        String sqlCommand = SQLCommands.getKeysByDbTableColumn(databaseName, tableName);

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

        return set;
    }

    private List<String> getNullableColumns(SQLExecutor executor) throws SQLException {

        String databaseName = request.getDatabaseName();
        String tableName = request.getDatabaseTable();

        String sqlCommand = SQLCommands.getGetColumnsNullableByDbAndTableName(databaseName, tableName);

        List<String> listOfColumns = executor.executeAndGetResultSetBuilder(sqlCommand).getStringsAtIndex(1);

        return listOfColumns;
    }


}

class ColumnMetadata{

    List<String> primaryKeyColumns;
    Set<String> foreignKeyColumns;
    List<String> nullableCollumns;

    public ColumnMetadata(List<String> primaryKeyColumns, Set<String> foreignKeyColumns, List<String> nullableCollumns) {
        this.primaryKeyColumns = primaryKeyColumns;
        this.foreignKeyColumns = foreignKeyColumns;
        this.nullableCollumns = nullableCollumns;
    }

    public List<String> getPrimaryKeyColumns() {
        return primaryKeyColumns;
    }

    public Set<String> getForeignKeyColumns() {
        return foreignKeyColumns;
    }

    public List<String> getNullableCollumns() {
        return nullableCollumns;
    }
}