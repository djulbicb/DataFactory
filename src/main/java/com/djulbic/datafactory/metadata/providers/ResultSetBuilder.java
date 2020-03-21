package com.djulbic.datafactory.metadata.providers;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ResultSetBuilder {
    ResultSet resultSet;

    ResultSetBuilder(ResultSet resultSet){
        this.resultSet = resultSet;
    }

    public List<String> getStringsAtIndex(int index) {
        List<String> strings = new ArrayList<>();
        try {
            while(resultSet.next()) {
                String databaseName = resultSet.getString(index);
                strings.add(databaseName);
            }
            resultSet.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return strings;
    }
}
