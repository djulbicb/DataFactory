package com.djulbic.datafactory.metadata.providers;

public class SQLCommands {
    private static final String GET_PRIMARY_COLUMNS_OF_TABLE = "SHOW KEYS FROM %s WHERE key_name = 'PRIMARY';";
    private static final String SHOW_DATABASES = "SHOW DATABASES";
    private static final String GET_TABLES_BY_DATABASE_NAME = "SHOW TABLES FROM %s;";
    private static final String GET_COLUMNS_BY_TABLE_NAME = "SHOW COLUMNS FROM %s;";
    private static final String GET_KEYS_BY_DB_TABLE_COLUMN = "SELECT * FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE WHERE (TABLE_SCHEMA='%s' OR REFERENCED_TABLE_SCHEMA='%s') AND (TABLE_NAME='%s' OR REFERENCED_TABLE_NAME='%s') AND (COLUMN_NAME='%s' OR REFERENCED_COLUMN_NAME='%s');";

    public static String getPrimaryColumnOfTable(String tableName) {
        return String.format(GET_PRIMARY_COLUMNS_OF_TABLE, tableName);
    }

    public static String getShowDatabases(){
        return SHOW_DATABASES;
    }

    public static String getTablesByDatabaseName(String databaseName) {
        return String.format(GET_TABLES_BY_DATABASE_NAME, databaseName);
    }

    public static String getColumnsByDatabaseAndTableName(String tableName) {
        return String.format(GET_COLUMNS_BY_TABLE_NAME, tableName);
    }

    public static String getKeysByDbTableColumn(String databaseName, String tableName, String columnName){
        return String.format(GET_KEYS_BY_DB_TABLE_COLUMN, databaseName, databaseName, tableName, tableName, columnName, columnName);
    }
}


/*
Komanda da se izlista keyevi
SELECT
  *
FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
WHERE
  TABLE_SCHEMA='assignment'
  AND
REFERENCED_TABLE_NAME = 'category';

* */