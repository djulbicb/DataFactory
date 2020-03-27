package com.djulbic.datafactory.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class ColumnSql {

    private boolean checked;
    private String name;
    private String type;
    private String size;

    @JsonProperty(value="isForeignKey")
    private boolean isForeignKey;

    @JsonProperty(value="isPrimaryKey")
    private boolean isPrimaryKey;

    @JsonProperty(value="isNullable")
    private boolean isNullable;
    private MethodDTO method;

    public ColumnSql() {
    }

    public ColumnSql(String name, String type, String size) {
        this(true, name, type, size, false, false, false, null);
    }

    public ColumnSql(boolean checked, String name, String type, String size, boolean isForeignKey, boolean isPrimaryKey, boolean isNullable, MethodDTO method) {
        this.checked = checked;
        this.name = name;
        this.type = type;
        this.size = size;
        this.isForeignKey = isForeignKey;
        this.isPrimaryKey = isPrimaryKey;
        this.isNullable = isNullable;
        this.method = method;
    }

    @Override
    public String toString() {
        return "ColumnSql{" +
                "checked=" + checked +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", size=" + size +
                ", isForeignKey=" + isForeignKey +
                ", isPrimaryKey=" + isPrimaryKey +
                ", isNullable=" + isNullable +
                ", method=" + method +
                '}';
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public boolean isForeignKey() {
        return isForeignKey;
    }

    public void setForeignKey(boolean foreignKey) {
        isForeignKey = foreignKey;
    }

    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        isPrimaryKey = primaryKey;
    }

    public boolean isNullable() {
        return isNullable;
    }

    public void setNullable(boolean nullable) {
        isNullable = nullable;
    }

    public MethodDTO getMethod() {
        return method;
    }

    public void setMethod(MethodDTO method) {
        this.method = method;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ColumnSql columnSql = (ColumnSql) o;
        return size == columnSql.size &&
                isForeignKey == columnSql.isForeignKey &&
                isPrimaryKey == columnSql.isPrimaryKey &&
                isNullable == columnSql.isNullable &&
                Objects.equals(name, columnSql.name) &&
                Objects.equals(type, columnSql.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, size, isForeignKey, isPrimaryKey, isNullable);
    }
}
