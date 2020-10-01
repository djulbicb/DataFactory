//package com.djulbic.datafactory.controllers;
//
//import com.djulbic.datafactory.DataLibraryMethodCallParser;
//import com.djulbic.datafactory.MapMySQLTypesToDataLibrary;
//import com.djulbic.datafactory.MethodCallParser;
//import com.djulbic.datafactory.model.*;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.node.ObjectNode;
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.google.gson.JsonElement;
//import data.DataLibrary;
//import data.DataLibraryLanguage;
//import data.DataLibraryMetadata;
//import org.apache.commons.lang3.StringUtils;
//import org.json.JSONArray;
//import org.json.JSONObject;
//import org.json.JSONTokener;
//import org.json.simple.parser.ParseException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import javax.el.MethodNotFoundException;
//import java.io.IOException;
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//
//import java.sql.SQLException;
//import java.time.LocalDateTime;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@RequestMapping("/api")
//@RestController
//public class PopulateController {
//
//    MapMySQLTypesToDataLibrary mapMySQLTypesToDataLibrary = new MapMySQLTypesToDataLibrary();
//    DataLibraryMethodCallParser parser = new DataLibraryMethodCallParser();
//
//    @Autowired
//    ObjectMapper mapper;
//    @Autowired
//    ConnectionManager manager;
//
//
//    @PostMapping("/getDatabaseRequestConfigPresetByPresetName")
//    public ExecuteRequestPreset getDatabaseRequestConfigPresetByPresetName(@RequestBody  ExecuteRequestPreset request) throws IOException {
//        return manager.getDatabaseRequestConfigPresetByPresetName(request);
//    }
//
//    @PostMapping("/getDatabaseRequestConfigPresets")
//    public List<ExecuteRequestPreset> getDatabaseRequestConfigPresets(@RequestBody  ExecuteRequestDTO request) throws IOException {
//        return manager.getDatabaseRequestConfigPreset(request);
//    }
//
//    @PostMapping("/getDatabaseRequestConfigPresetsAsStringList")
//    public List<String> getDatabaseRequestConfigPresetsAsStringList(@RequestBody  ExecuteRequestDTO request) throws IOException {
//        return manager.getDatabaseRequestConfigPresetAsStringList(request);
//    }
//
//    @PostMapping("/addDatabaseRequestConfigPreset")
//    public void addDatabaseRequestConfigPreset(@RequestBody ExecuteRequestPreset preset) throws IOException {
//        manager.addDatabaseRequestConfigPreset(preset);
//    }
//
//    @PostMapping("/removeDatabaseRequestConfigPreset")
//    public List<String> removeDatabaseRequestConfigPreset(@RequestBody ExecuteRequestPreset preset) throws IOException {
//        manager.removeDatabaseRequestConfigPreset(preset);
//        return manager.getDatabaseRequestConfigPresetAsStringList(preset.getRequest());
//    }
//
//    @GetMapping("/getMappedSQLTypesToDataLibraryMethods")
//    public Map<String, List<MethodDTO>> getMappedSQLTypesToDataLibraryMethods(){
//        return mapMySQLTypesToDataLibrary.getMappedSQLTypesToDataLibraryMethods();
//    }
//
//    private String getInsertQueryStatement(ExecuteRequestDTO request) {
//        String databaseName = request.getConfig().getDatabaseName();
//        String databaseTable = request.getConfig().getDatabaseTable();
//        String dbTable = String.format("`%s`.`%s`", databaseName, databaseTable);
//        String insertQuery = "INSERT INTO %s (%s) VALUES (%s);";
//        List<String> columnNames = new ArrayList<>();
//        List<Object> columnValues = new ArrayList<>();
//        DataLibraryMetadata metadata = new DataLibraryMetadata();
//        DataLibrary data = DataLibraryMap.getDataLibrary(request.getConfig().getLanguage());
//
//        for (ColumnSql sql : request.getColumns()) {
//            String columnName = sql.getName();
//            // Skip iteration if column is not checked for processing
//            if (!sql.isChecked()) {
//                continue;
//            }
//            MethodDTO method = sql.getMethod();
//            if (method != null){
//                String name = method.getMethodName();
//                String inputParametars = method.getInputParametars();
//                String inputDelimiter = method.getInputDelimiter();
//                if (inputDelimiter == null || inputDelimiter.isEmpty()){
//                    inputDelimiter = ",";
//                }
//                name = name + "(" + inputParametars + ")";
//
//                Object parseValue = parser.parse(data, name, inputDelimiter);
//
//                columnNames.add("`" + sql.getName() + "`");
//
//                if (parseValue.getClass().equals(String.class)){
//                    columnValues.add("'" + escapeStringForMySQL(parseValue.toString()) + "'");
//                }else{
//                    columnValues.add(parseValue);
//                }
//
////                if(sql.getType().contains("VARCHAR")){
////                    columnValues.add("'" + parseValue + "'");
////                }else{
////                    columnValues.add(parseValue);
////                }
//            }
//            System.out.println("-----" + sql.getName());
//        }
//        System.out.println(columnNames);
//        System.out.println(columnValues);
//        String col = StringUtils.join(columnNames, ",");
//        String val = StringUtils.join(columnValues, ",");
//
//        String query = String.format(insertQuery, dbTable, col, val);
//        return query;
//    }
//
//    private String escapeStringForMySQL(String s) {
//        return s.replaceAll("\\\\", "\\\\\\")
//                .replaceAll("\b","\\b")
//                .replaceAll("\n","\\n")
//                .replaceAll("\r", "\\r")
//                .replaceAll("\t", "\\t")
//                .replaceAll("\\x1A", "\\Z")
//                .replaceAll("\\x00", "\\0")
//                .replaceAll("'", "\\'")
//                .replaceAll("\"", "\\\"");
//    }
//
//    private String escapeWildcardsForMySQL(String s) {
//        return escapeStringForMySQL(s)
//                .replaceAll("%", "\\%")
//                .replaceAll("_","\\_");
//    }
//
//
//    @PostMapping("/getdata")
//    public String getData(
//            @RequestBody(required = false) String json,
//            @RequestParam(required = false, defaultValue = "ENGLISH") String language)
//            throws InvocationTargetException, IllegalAccessException {
//
//        JSONTokener token = new JSONTokener(json);
//        Object next = token.nextValue();
//        JsonParse parse = new JsonParse(DataLibraryMap.getDataLibrary(language));
//        Object o = parse.parseJson(next);
//        System.out.println(o);
//
//        return o.toString();
//    }
//
//    @PostMapping(value = "/getdata/{x}")
//    public String getDataList(
//            @RequestBody(required = false) String json,
//            @PathVariable("x") int count,
//            @RequestParam(required = false, defaultValue = "ENGLISH") String language) throws InvocationTargetException, IllegalAccessException {
//
//        DataLibrary data = DataLibraryMap.getDataLibrary(language);
//        JsonParse parse = new JsonParse(data);
//        List<Object> response = new ArrayList<>();
//        JSONArray array = new JSONArray();
//
//        for (int i = 0; i < count; i++) {
//            JSONTokener token = new JSONTokener(json);
//            Object next = token.nextValue();
//            Object o = parse.parseJson(next);
//            //response.add(o);
//            array.put(o);
//        }
//
//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        JsonElement jsonElement = gson.toJsonTree(response);
//
//        //return gson.toJson(response);
//
//
//        return array.toString();
//    }
//
//    @GetMapping("/getDataLibraryMethod")
//    public List<String> getDataLibraryMethod(){
//        System.out.println("Request" + LocalDateTime.now());
//        DataLibraryMetadata metadata = new DataLibraryMetadata();
//        return metadata.getExposedMethods().stream().map(method -> method.getName()).collect(Collectors.toList());
//    }
//
//
////      ([^(]*)   - up until (
////      \((.*)\)  - between paranthesis
//
//    private String getMethodNameFromString(String methodName){
//        return "";
//    }
//
//    private Object[] getMethodParamsFromString(String methodName){
//        return null;
//    }
//
//}
//
//class DataLibraryMap {
//    static Map<String, DataLibrary> mapDataLibrary = new LinkedHashMap<>();
//
//    static DataLibrary getDataLibrary(String language){
//        if (!mapDataLibrary.containsKey(language)){
//            DataLibrary data = DataLibrary.getData(language);
//            mapDataLibrary.put(language, data);
//        }
//        return mapDataLibrary.get(language);
//    }
//}
//
//class JsonParse{
//    private DataLibrary dataLibrary;
//    public JsonParse(DataLibrary dataLibrary) {
//        this.dataLibrary = dataLibrary;
//    }
//
//    public Object parseJson(Object next) throws InvocationTargetException, IllegalAccessException {
//        if (next instanceof JSONObject){
//            System.out.println("Object");
//            JSONObject nextObj = (JSONObject) next;
//            Set<String> strings = nextObj.keySet();
//            System.out.println(strings);
//
//            for (String key : strings) {
//                Object object = nextObj.get(key);
//                System.out.println("Object ---- " + key);
//                System.out.println(object);
//                Object o = parseJson(object);
//                nextObj.put(key, o);
//            }
//            return nextObj;
//        }else if(next instanceof  JSONArray){
//            System.out.println("Array");
//            JSONArray array = (JSONArray) next;
//            JSONArray objects = new JSONArray();
//            array.forEach(o -> {
//                System.out.println("Array ----");
//                System.out.println(o);
//                try {
//                    Object parsed = parseJson(o);
//                    objects.put(parsed);
//                } catch (InvocationTargetException e) {
//                    e.printStackTrace();
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                }
//            });
//            return objects;
//        } else{
//            System.out.println("Something else ---");
//            System.out.println(next);
//
//            if (next.toString().contains("(") && next.toString().contains(")")){
//                Method methodByName = getMethodByName(next.toString(), DataLibrary.class);
//                MethodCallParser parser = new DataLibraryMethodCallParser();
//                return parser.parse(dataLibrary, next.toString(), ",");
//                //return parser.parse(data, next.toString(), ",");
//                // return methodByName.invoke(data);
//            }else{
//                return next.toString();
//            }
//
//
//        }
//    }
//
//    public Method getMethodByName(String methodName, Class scanClass){
//        methodName = methodName.substring(0, methodName.indexOf("("));
//        Method[] methods = scanClass.getDeclaredMethods();
//        for (Method method : methods) {
//            if (methodName.equalsIgnoreCase(method.getName())){
//                System.out.println("found");
//                return method;
//            }
//        }
//        throw new MethodNotFoundException();
//    }
//
//}
//
////@RestController
////public class GreetingController {
////
////    private static final String template = "Hello, %s!";
////    private final AtomicLong counter = new AtomicLong();
////
////    @GetMapping("/greeting")
////    public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
////        return new Greeting(counter.incrementAndGet(), String.format(template, name));
////    }
////}