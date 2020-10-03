package com.djulb.datafactory.parser;


import com.djulb.datafactory.parser.MethodCallParser;
import data.DataLibrary;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

public class JsonParserDL {
    private DataLibrary dataLibrary;
    public JsonParserDL(DataLibrary dataLibrary) {
        this.dataLibrary = dataLibrary;
    }

    public Object parseJson(String json) throws InvocationTargetException, IllegalAccessException {
        JSONTokener token = new JSONTokener(json);
        Object next = token.nextValue();
        return parseJson(next);
    }

    public Object parseJson(Object next) throws InvocationTargetException, IllegalAccessException {
        if (next instanceof JSONObject){
            JSONObject nextObj = (JSONObject) next;
            Set<String> strings = nextObj.keySet();
            for (String key : strings) {
                Object o = parseJson(nextObj.get(key));
                nextObj.put(key, o);
            }
            return nextObj;
        }else if(next instanceof JSONArray){
            JSONArray objects = new JSONArray();
            JSONArray array = (JSONArray) next;
            array.forEach(o -> {
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
            if (next.toString().contains("(") && next.toString().contains(")")){
                MethodCallParser parser = new MethodCallParser();
                return parser.parse(dataLibrary, next.toString(), ",");
            }else{
                return next.toString();
            }
        }
    }
}