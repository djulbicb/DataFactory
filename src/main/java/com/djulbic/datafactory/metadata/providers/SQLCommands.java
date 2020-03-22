package com.djulbic.datafactory.metadata.providers;

class SQLCommands {
    private static final String GET_PRIMARY_COLUMNS_OF_TABLE = "SHOW KEYS FROM %s WHERE key_name = 'PRIMARY';";
    private static final String SHOW_DATABASES = "SHOW DATABASES";
    private static final String GET_TABLES_BY_DATABASE_NAME = "SHOW TABLES FROM %s;";
    private static final String GET_COLUMNS_BY_TABLE_NAME = "SHOW COLUMNS FROM %s;";
    //private static final String GET_KEYS_BY_DB_TABLE_COLUMN = "SELECT * FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE WHERE (TABLE_SCHEMA='%s' OR REFERENCED_TABLE_SCHEMA='%s') AND (TABLE_NAME='%s' OR REFERENCED_TABLE_NAME='%s') AND (COLUMN_NAME='%s' OR REFERENCED_COLUMN_NAME='%s');";
    private static final String GET_KEYS_BY_DB_TABLE_COLUMN = "SELECT TABLE_SCHEMA, TABLE_NAME, COLUMN_NAME, REFERENCED_TABLE_SCHEMA, REFERENCED_TABLE_NAME, REFERENCED_COLUMN_NAME FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE WHERE (TABLE_SCHEMA='%s' OR REFERENCED_TABLE_SCHEMA='%s') AND (TABLE_NAME='%s' OR REFERENCED_TABLE_NAME='%s') AND (REFERENCED_TABLE_NAME IS NOT NULL);";
    private static final String GET_COLUMNS_NULLABLE_BY_DB_AND_TABLE_NAME = "SELECT COLUMN_NAME FROM information_schema.COLUMNS WHERE TABLE_SCHEMA='%s' AND TABLE_NAME='%s' AND IS_NULLABLE='YES';";



    public static String getPrimaryColumnOfTable(String databaseTableName) {
        return String.format(GET_PRIMARY_COLUMNS_OF_TABLE, databaseTableName);
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

    public static String getKeysByDbTableColumn(String databaseName, String tableName){
        return String.format(GET_KEYS_BY_DB_TABLE_COLUMN, databaseName, databaseName, tableName, tableName);
    }

    public static String getGetColumnsNullableByDbAndTableName(String databaseName, String tableName){
        return String.format(GET_COLUMNS_NULLABLE_BY_DB_AND_TABLE_NAME, databaseName, tableName);
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