package com.djulbic.datafactory;

import com.djulbic.datafactory.model.MethodDTO;
import data.DataLibrary;
import data.DataLibraryMetadata;
import reader.JsonResourceReader;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MapMySQLTypesToDataLibrary extends JsonResourceReader<Map> {
    private Map map;
    private Method[] dlMethods;
    DataLibraryMetadata metadata = new DataLibraryMetadata();

    public MapMySQLTypesToDataLibrary() {
        map = readConfig(Map.class);
        dlMethods = DataLibrary.class.getMethods();
    }

    private Method getDlMethod(String methodName){
        for (Method method : dlMethods) {
            if (method.getName().equalsIgnoreCase(methodName)){
                return method;
            }
        }
        throw new IllegalArgumentException("Cant find method with that name");
    }

    private List<String> getPrimitiveJavaReturnType(String specifiedMySqlType){
        System.out.println(map);
        return (List<String>) map.get(specifiedMySqlType);
    }

    public Map<String, List<MethodDTO>> getMappedSQLTypesToDataLibraryMethods(){
        Map<String, List<MethodDTO>> methods = new LinkedHashMap<>();
        Map<String, List<String>> sqlJavaTypesMap = this.map;

        for (Map.Entry<String, List<String>> entry : sqlJavaTypesMap.entrySet()) {
            String sqlType = entry.getKey();
            List<String> javaTypes = entry.getValue();

            List<Method> methodsThatReturnType = metadata.getMethodsThatReturnType(javaTypes.toArray(new String[0]));

            List<MethodDTO> methodDTOS = new ArrayList<>();
            for (Method method : methodsThatReturnType) {
                MethodDTO dto = new MethodDTO();
                dto.setMethodName(method.getName());
                dto.setParamsCount(method.getParameterCount());
                dto.setVarArgs(method.isVarArgs());
                methodDTOS.add(dto);
            }
            methods.put(sqlType, methodDTOS);
        }


        return methods;
    }

    public List<Method> getMethods(String specifiedMySqlType){
        List<Method> methodsThatReturnSpecifiedType = new ArrayList<>();

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
