package com.djulbic.datafactory.model;


import com.djulbic.datafactory.MethodCallParser;
import data.DataLibrary;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.el.MethodNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

public class JsonParserDL {
    private DataLibrary dataLibrary;
    public JsonParserDL(DataLibrary dataLibrary) {
        this.dataLibrary = dataLibrary;
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
        }else if(next instanceof JSONArray){
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

            if (next.toString().contains("(") && next.toString().contains(")")){
                System.out.println(next.toString());
                Method methodByName = getMethodByName(next.toString(), DataLibrary.class);
                MethodCallParser parser = new MethodCallParser();
                return parser.parse(dataLibrary, next.toString(), ",");
            }else{
                return next.toString();
            }


        }
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