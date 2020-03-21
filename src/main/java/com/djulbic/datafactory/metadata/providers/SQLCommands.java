package com.djulbic.datafactory.metadata.providers;

public class SQLCommands {
    private static final String GET_PRIMARY_COLUMNS_OF_TABLE = "SHOW KEYS FROM %s WHERE key_name = 'PRIMARY';";
    private static final String SHOW_DATABASES = "SHOW DATABASES";
    private static final String GET_TABLES_BY_DATABASE_NAME = "SHOW TABLES FROM %s;";
    private static final String GET_COLUMNS_BY_TABLE_NAME = "SHOW COLUMNS FROM %s;";
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
}
