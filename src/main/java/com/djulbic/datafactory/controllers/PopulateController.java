package com.djulbic.datafactory.controllers;

import com.djulbic.datafactory.DataLibraryMethodCallParser;
import com.djulbic.datafactory.MapMySQLTypesToDataLibrary;
import com.djulbic.datafactory.MethodCallParser;
import com.djulbic.datafactory.metadata.providers.MySQLMetadataService;
import com.djulbic.datafactory.model.ColumnSql;
import com.djulbic.datafactory.model.DatabaseRequestConfig;
import com.djulbic.datafactory.model.ExecuteRequestDTO;
import com.djulbic.datafactory.model.MethodDTO;
import data.DataLibrary;
import data.DataLibraryLanguage;
import data.DataLibraryMetadata;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.el.MethodNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RequestMapping("/api")
@RestController
public class PopulateController {

    DataLibrary data = DataLibrary.getEnglishData();
    MapMySQLTypesToDataLibrary mapMySQLTypesToDataLibrary = new MapMySQLTypesToDataLibrary();
    DataLibraryMethodCallParser parser = new DataLibraryMethodCallParser();

    @Autowired
    MySQLMetadataService mysqlProvider;

    @GetMapping("/getMappedSQLTypesToDataLibraryMethods")
    public Map<String, List<MethodDTO>> getMappedSQLTypesToDataLibraryMethods(){
        return mapMySQLTypesToDataLibrary.getMappedSQLTypesToDataLibraryMethods();
    }

    @PostMapping("/execute")
    public String execute(@RequestBody(required = false) ExecuteRequestDTO request){
        System.out.println(request);
        String databaseName = request.getConfig().getDatabaseName();
        String databaseTable = request.getConfig().getDatabaseTable();
        String dbTable = String.format("`%s`.`%s`", databaseName, databaseTable);
        String insertQuery = "INSERT INTO %s (%s) VALUES (%s);";

        List<String> columnNames = new ArrayList<>();
        List<Object> columnValues = new ArrayList<>();

        DataLibraryMetadata metadata = new DataLibraryMetadata();

        for (ColumnSql sql : request.getColumns()) {

            // Skip iteration if column is not checked for processing
            if (!sql.isChecked()) {
                continue;
            }

            MethodDTO method = sql.getMethod();

            if (method != null){
                String name = method.getMethodName();
                String inputParametars = method.getInputParametars();
                name = name + "(" + inputParametars + ")";

                Object parseValue = parser.parse(data, name);

                columnNames.add("`" + sql.getName() + "`");

                if(sql.getType().contains("VARCHAR")){
                    columnValues.add("'" + parseValue + "'");
                }else{
                    columnValues.add(parseValue);
                }


            }
            System.out.println("-----" + sql.getName());
        }
        System.out.println(columnNames);
        System.out.println(columnValues);
        String col = StringUtils.join(columnNames, ",");
        String val = StringUtils.join(columnValues, ",");

        String query = String.format(insertQuery, dbTable, col, val);
        System.out.println(query);

        //mysqlProvider.insertQuery(query);

        return "sss";
    }

    @PostMapping("/getColumns")
    public List<ColumnSql> getColumns(@RequestBody DatabaseRequestConfig databaseRequestConfig) throws SQLException {
        System.out.println(databaseRequestConfig);

        List<ColumnSql> columns = mysqlProvider.getColumns(databaseRequestConfig.getDatabaseName(), databaseRequestConfig.getDatabaseTable());
        System.out.println(columns);

        return columns;
    }

    @PostMapping("/getdata")
    public String getData(@RequestBody(required = false) String json) throws InvocationTargetException, IllegalAccessException {
        JSONTokener token = new JSONTokener(json);

        Object next = token.nextValue();
        Object o = parseJson(next);
        System.out.println(o);
        return o.toString();
    }

    @GetMapping("/getDataLibraryMethod")
    public List<String> getDataLibraryMethod(){
        System.out.println("Request" + LocalDateTime.now());
        DataLibraryMetadata metadata = new DataLibraryMetadata();
        return metadata.getExposedMethods().stream().map(method -> method.getName()).collect(Collectors.toList());
    }

    @GetMapping("/getDatabases")
    public List<String> getDataBases(){
        return mysqlProvider.getDatabases();
    }

    @PostMapping("/getTables")
    public List<String> getDataBasesTables(@RequestBody DatabaseRequestConfig requestConfig){
        System.out.println("getTables");
        System.out.println(requestConfig);
        System.out.println("----");
        List<String> tables = mysqlProvider.getTables(requestConfig.getDatabaseName());

        return tables;
    }

    @GetMapping("/getDataLibraryLanguages")
    public List<String> getDataLibraryLanguages(){
        List<String> collect = Arrays.stream(DataLibraryLanguage.values()).map(dataLibraryLanguage -> dataLibraryLanguage.toString()).collect(Collectors.toList());
        return collect;
    }

    @PostMapping("/getdata/{x}")
    public List<String> getDataList(@RequestBody(required = false) String json, @PathVariable("x") int count) throws InvocationTargetException, IllegalAccessException {
        List<String> response = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            JSONTokener token = new JSONTokener(json);
            Object next = token.nextValue();
            Object o = parseJson(next);
            response.add(o.toString());
        }

        return response;
    }

    public Object parseJson(Object next) throws InvocationTargetException, IllegalAccessException {
        if (next instanceof JSONObject){
            System.out.println("Object");
            JSONObject nextObj = (JSONObject) next;
            Set<String> strings = nextObj.keySet();
            System.out.println(strings);

            for (String key : strings) {
                Object object = nextObj.get(key);
                System.out.println("Object ---- " + key);
                System.out.println(object);
                Object o = parseJson(object);
                nextObj.put(key, o);
            }

            return nextObj;

        }else if(next instanceof  JSONArray){
            System.out.println("Array");
            JSONArray array = (JSONArray) next;
            JSONArray objects = new JSONArray();
            array.forEach(o -> {
                System.out.println("Array ----");
                System.out.println(o);
                try {
                    Object parsed = parseJson(o);
                    objects.put(parsed);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            });
            return objects;
        } else{
            System.out.println("Something else ---");
            System.out.println(next);
            Method methodByName = getMethodByName(next.toString(), DataLibrary.class);

            MethodCallParser parser = new DataLibraryMethodCallParser();
            return parser.parse(data, next.toString());
            // return methodByName.invoke(data);
        }
    }

//      ([^(]*)   - up until (
//      \((.*)\)  - between paranthesis

    private String getMethodNameFromString(String methodName){
        return "";
    }

    private Object[] getMethodParamsFromString(String methodName){
        return null;
    }


    public Method getMethodByName(String methodName, Class scanClass){
        methodName = methodName.substring(0, methodName.indexOf("("));
        Method[] methods = scanClass.getDeclaredMethods();
        for (Method method : methods) {
            if (methodName.equalsIgnoreCase(method.getName())){
                System.out.println("found");
                return method;
            }
        }
        throw new MethodNotFoundException();
    }
}

//@RestController
//public class GreetingController {
//
//    private static final String template = "Hello, %s!";
//    private final AtomicLong counter = new AtomicLong();
//
//    @GetMapping("/greeting")
//    public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
//        return new Greeting(counter.incrementAndGet(), String.format(template, name));
//    }
//}