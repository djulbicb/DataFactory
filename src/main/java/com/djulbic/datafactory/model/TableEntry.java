package com.djulbic.datafactory.model;

public class TableEntry {
    boolean enabled;
    String columnName;
    String columnType;
    String methodCall;

    boolean acceptInput;
    boolean acceptVarargs;

    public static TableEntry test(){
        TableEntry entry = new TableEntry();
        entry.setEnabled(true);
        entry.setMethodCall("getName()");
        entry.setColumnType("STRING");
        entry.setAcceptInput(false);
        entry.setAcceptVarargs(false);
        entry.setColumnName("name");
        return entry;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public String getMethodCall() {
        return methodCall;
    }

    public void setMethodCall(String methodCall) {
        this.methodCall = methodCall;
    }

    public boolean isAcceptInput() {
        return acceptInput;
    }

    public void setAcceptInput(boolean acceptInput) {
        this.acceptInput = acceptInput;
    }

    public boolean isAcceptVarargs() {
        return acceptVarargs;
    }

    public void setAcceptVarargs(boolean acceptVarargs) {
        this.acceptVarargs = acceptVarargs;
    }
}
