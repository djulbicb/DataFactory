package com.djulbic.datafactory;

import data.DataLibraryMetadata;
import reader.JsonResourceReader;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapMySQLTypesToDataLibrary extends JsonResourceReader<Map> {
    private Map map;
    public MapMySQLTypesToDataLibrary() {
        map = readConfig(Map.class);
    }

    private List<String> getPrimitiveJavaReturnType(String specifiedMySqlType){
        System.out.println(map);
        return (List<String>) map.get(specifiedMySqlType);
    }

    public List<Method> getMethods(String specifiedMySqlType){
        List<Method> methodsThatReturnSpecifiedType = new ArrayList<>();

        DataLibraryMetadata metadata = new DataLibraryMetadata();
        List<String> dataLibraryTypes = getPrimitiveJavaReturnType(specifiedMySqlType);
        System.out.println(dataLibraryTypes);
        for (String libraryType : dataLibraryTypes) {
            methodsThatReturnSpecifiedType.addAll(metadata.getMethodsThatReturnType(libraryType));
        }
        return methodsThatReturnSpecifiedType;
    }

    @Override
    public String getResourceFilePath() {
        return "mysqltypes.json";
    }
}
