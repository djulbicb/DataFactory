package com.djulbic.datafactory.controllers;

import com.djulbic.datafactory.DataLibraryMethodCallParser;
import com.djulbic.datafactory.MethodCallParser;
import data.DataLibrary;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.web.bind.annotation.*;

import javax.el.MethodNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RequestMapping("/api")
@RestController
public class PopulateController {

    DataLibrary data = DataLibrary.getEnglishData();

    @PostMapping("/getdata")
    public String getData(@RequestBody(required = false) String json) throws InvocationTargetException, IllegalAccessException {
        JSONTokener token = new JSONTokener(json);


        Object next = token.nextValue();
        Object o = parseJson(next);
        System.out.println(o);
        return o.toString();
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
