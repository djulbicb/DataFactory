package com.djulbic.datafactory;



import com.djulbic.datafactory.metadata.providers.MySQLMetadataService;
import com.djulbic.datafactory.model.ColumnSql;

import com.djulbic.datafactory.model.MethodDTO;
import org.springframework.boot.jdbc.DataSourceBuilder;

import javax.el.MethodNotFoundException;
import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TestMain {


    public static void main(String[] args) throws SQLException, InvocationTargetException, IllegalAccessException {

        String connectionUrl = "jdbc:mysql://localhost:3306";
        String username = "root";
        String password = "";

        MySQLMetadataService service = new MySQLMetadataService(connectionUrl, username, password);
        service.test("assigment", "category");

//        MapMySQLTypesToDataLibrary map = new MapMySQLTypesToDataLibrary();
//        Map<String, List<MethodDTO>> map1 = map.getMappedSQLTypesToDataLibraryMethods();
//        System.out.println(map1);

//        DataLibrary dl = DataLibrary.getEnglishData();
//      DataLibraryMethodCallParser methodCallParser = new DataLibraryMethodCallParser();
//        Object parse = methodCallParser.parse(dl, "getDoubleInRange(3,5)");
//        System.out.println(parse);




    }

    private static Object[] getParams(Method method, List<String> extractedParams){
        System.out.println(method.getName());
        System.out.println(method.getParameterCount());

        int parametarCount = method.getParameterCount();
        Object[] methodParams = new Object[parametarCount];
        if (parametarCount > 0){

            for (int i = 0; i < method.getParameters().length; i++) {
                Parameter parameter = method.getParameters()[i];
                Class<?> type = parameter.getType();

                if (type == int.class){
                    System.out.println("type - int");
                    System.out.println(extractedParams.get(i));
                    methodParams[i] = Integer.parseInt(extractedParams.get(i));
                }else if(type == float.class){
                    System.out.println("type - float");
                    methodParams[i] = Float.parseFloat(extractedParams.get(i));
                }else if(type == double.class){
                    System.out.println("type - double");
                    methodParams[i] = Double.parseDouble(extractedParams.get(i));
                } else if (type == String.class){
                    System.out.println("type - String");
                    methodParams[i] = extractedParams.get(i);
                } else if(parameter.isVarArgs()){
                    System.out.println("type - var args");
                    methodParams[i] = extractedParams.toArray();
                } else{
                    System.out.println("type - not find");
                }
            }

        }
        for (Parameter parameter : method.getParameters()) {
            System.out.println(parameter.getType());
        }
        System.out.println("---");
        return methodParams;
    }

    private static String getMethodNameSubstrFromString(String methodCall){
        if (methodCall.contains("(") && methodCall.contains(")")){
            String methondName = methodCall.substring(0, methodCall.indexOf("("));
            return methondName.trim();
        }
        return methodCall;
    }

    private static String getMethodParamsSubstrFromString(String methodCall){
        if (methodCall.contains("(") && methodCall.contains(")")){
            methodCall = methodCall.substring(methodCall.indexOf("(")+1, methodCall.lastIndexOf(")") );
            return methodCall.trim();
        }
        return methodCall;
    }

    public static List<String> getParams(String tested){
        List<String> tokensList = new ArrayList<String>();
        boolean inQuotes = false;
        StringBuilder b = new StringBuilder();
        for (char c : tested.toCharArray()) {
            switch (c) {
                case ',':
                    if (inQuotes) {
                        b.append(c);
                    } else {
                        tokensList.add(b.toString().trim());
                        b = new StringBuilder();
                    }
                    break;
                case '\"':
                    inQuotes = !inQuotes;
                default:
                    b.append(c);
                    break;
            }
        }
        tokensList.add(b.toString().trim());
        return tokensList;
    }

    public static Method getMethodByName(String methodName, Class scanClass){
        List<Method> methods = new ArrayList<>();
        Method[] declaredMethods = scanClass.getDeclaredMethods();
        for (Method method : declaredMethods) {
            if (method.getName().equalsIgnoreCase(methodName)){
                return method;
            }
        }
        throw new MethodNotFoundException("Method " + methodName + " not found");
    }


    public static List<ColumnSql> getColumnsMetadata(DatabaseMetaData metaData) throws SQLException {
        ResultSet resultSet = metaData.getColumns(null, null, "bojan", null); // ovo je db
        List<ColumnSql> columnSql = new ArrayList<>();
        while (resultSet.next()) {
            String name = resultSet.getString("COLUMN_NAME");
            String type = resultSet.getString("TYPE_NAME");
            String size = resultSet.getString("COLUMN_SIZE");

            ColumnSql column = new ColumnSql(name, type, size);

            columnSql.add(column);
        }
        return columnSql;
    }

    public static DataSource getDataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName(com.mysql.cj.jdbc.Driver.class.getName()); //"com.mysql.jdbc.Driver"
        dataSourceBuilder.url("jdbc:mysql://localhost:3306");//bojan?createDatabaseIfNotExist=true");
        dataSourceBuilder.username("root");
        dataSourceBuilder.password("");
        return dataSourceBuilder.build();
    }
}



//        String methodNameSubstr = getMethodNameSubstrFromString(m);
//        String paramSubstr = getMethodParamsSubstrFromString(m);
//        List<String> extractedParams = getParams(paramSubstr);
//
//        System.out.println(methodNameSubstr);
//        System.out.println(extractedParams);
//
//        Method methodByName = getMethodByName(methodNameSubstr, DataLibrary.class);
//        Object[] params = getParams(methodByName, extractedParams);
//
//        System.out.println(Arrays.toString(params));
//
//        Object invoke = methodByName.invoke(dl, params);
//        System.out.println("Return: " + invoke.toString());




//        String connectionUrl = "jdbc:mysql://localhost:3306";
//        String username = "root";
//        String password = "";
//        MySQLMetadataProvider metadataProvider = new MySQLMetadataProvider(connectionUrl, username, password);
//        List<String> databases = metadataProvider.getDatabases();
//        System.out.println(databases);
//
//        List<String> test = metadataProvider.getTables("test");
//        System.out.println(test);
//
//        List<ColumnSql> columns = metadataProvider.getColumns("test", "bojan");
//        System.out.println(columns);

//       MapMySQLTypesToDataLibrary conversion = new MapMySQLTypesToDataLibrary();
//        System.out.println(conversion);
//        List<Method> string = conversion.getMethods("INTEGER");
//        for (Method s : string) {
//            System.out.println(s);
//        }
//
//
//        System.out.println(Types.ARRAY);
//        DataLibrary dataLibrary = DataLibrary.getEnglishData();
//
//        // getDouble(double minBound, double maxBound)
//        String methodCall = "pickRandom ( true, 'SSS', 'Bojan') ";
//        MethodCallParser callParser = new DataLibraryMethodCallParser();
//        callParser.parse(dataLibrary, methodCall);

//        DataLibraryMetadata meta = new DataLibraryMetadata();
//        List<Method> type = meta.getMethodsThatReturnType("string", "int");
//        for (Method method : type) {
//            System.out.println(method);
//        }
