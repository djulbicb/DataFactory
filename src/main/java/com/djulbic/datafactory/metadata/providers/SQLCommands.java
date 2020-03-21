package com.djulbic.datafactory.metadata.providers;

public class SQLCommands {
    private static final String PRIMARY_COLUMN_OF_TABLE = "SHOW KEYS FROM %s WHERE key_name = 'PRIMARY';";

    public static String getPrimaryColumnOfTable(String tableName) {
        return String.format(PRIMARY_COLUMN_OF_TABLE, tableName);
    }
}
