package com.djulbic.datafactory.controllers;

import com.djulbic.datafactory.model.Api;
import com.djulbic.datafactory.model.JsonParserDL;
import com.google.gson.*;
import data.DataLibrary;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.JSONArray;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Controller
public class DataFactoryController {

    private static final int DEFAULT_API_ITEM_COUNT = 1;
    private static final int DEFAULT_API_WAIT_TIME = 0;
    private static final List<String> RESERVED_WORDS = Arrays.asList("apiName", "apiWait", "apiCount", "apiId");
    private static final String DEFAUL_API_ID = "apiId";

    private Map<String, Api> apiMap = new HashMap();


    @GetMapping("/get/{apiName}")
    public ResponseEntity<String> get ( @PathVariable(name = "apiName") String apiName) {
        Api apiConfig = apiMap.get(apiName);
        return ResponseEntity.ok(apiConfig.toString());
    }

    @GetMapping("/get/{apiName}/{apiId}")
    public ResponseEntity<String> getOne (
            @PathVariable(name = "apiName") String apiName,
            @PathVariable(name = "apiId") String apiId) {
        Api apiConfig = apiMap.get(apiName);

        for (Object obj : apiConfig.getData()) {
            if (obj instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) obj;
                Object o = jsonObject.get(apiConfig.getId());

                if (o.toString().equals(apiId)) {
                    return ResponseEntity.ok(jsonObject.toString());
                }
            } else if (obj instanceof org.json.JSONArray) {
                JSONArray array = (JSONArray) obj;
                int index = Integer.parseInt(apiId);
                return ResponseEntity.ok(array.get(index).toString());


            }
        }

        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/set/{apiName}")
    public ResponseEntity<String> set (
            HttpServletRequest request,
            @PathVariable(name = "apiName") String apiName,
            @RequestParam(name = "apiWait", required = false, defaultValue = "0") int apiWait,
            @RequestParam(name = "apiCount", required = false, defaultValue = "100") int apiCount) throws InvocationTargetException, IllegalAccessException {
        JsonObject object = new JsonObject();

        Map<String, String[]> map = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : map.entrySet()) {
            String key = entry.getKey();

            if (RESERVED_WORDS.contains(key)) {
                continue;
            }

            String paramValue[] = entry.getValue();

            String value = "";
            if (paramValue.length > 1) {
                value = Arrays.toString(paramValue);
            } else if (paramValue.length == 1) {
                value = paramValue[0];
            } else {
                value = "";
            }

            object.addProperty(key, value);
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(object);

        System.out.println(json);
        JSONArray parse = parse(json, DEFAUL_API_ID, apiCount);

        Api api = new Api.Builder()
                .withName(apiName)
                .withWait(apiWait)
                .withData(parse)
                .build();

        apiMap.put(apiName, api);

        return ResponseEntity.ok(json);
    }

    @PostMapping("/set/{apiName}")
    public ResponseEntity<String> getDataList(
            HttpServletRequest request,
            @RequestBody(required = false) String json,
            @PathVariable(name = "apiName") String apiName
        ) throws InvocationTargetException, IllegalAccessException {
        String jsonForParsing = json;
        int apiWait = DEFAULT_API_WAIT_TIME;
        int apiCount = DEFAULT_API_ITEM_COUNT;
        String apiId = DEFAUL_API_ID;

        JsonElement element = JsonParser.parseString(json);
        if (element instanceof JsonObject) {
            JsonObject elementObj = (JsonObject) element;
            for (String reservedWord : RESERVED_WORDS) {

                if (elementObj.has(reservedWord)) {
                    switch (reservedWord) {
                        case "apiWait":
                            apiWait = elementObj.get("apiWait").getAsInt();
                            break;
                        case "apiCount":
                            apiCount = elementObj.get("apiCount").getAsInt();
                            break;
                        case "apiId":
                            apiId = elementObj.get("apiId").getAsString();
                            break;
                    }
                    elementObj.remove(reservedWord);
                }
            }
            jsonForParsing = elementObj.toString();
        }

        JSONArray parse = parse(jsonForParsing, apiId, apiCount);

        Api api = new Api.Builder()
                .withName(apiName)
                .withId(apiId)
                .withWait(apiWait)
                .withData(parse)
                .build();
        apiMap.put(apiName, api);

        return ResponseEntity.ok(parse.toString());
    }

    private JSONArray parse(String json, String apiId, int count) throws InvocationTargetException, IllegalAccessException {
        DataLibrary data = DataLibrary.getEnglishData();

        JsonParserDL parse = new JsonParserDL(data);
        JSONArray array = new JSONArray();

        for (int i = 0; i < count; i++) {
            JSONTokener token = new JSONTokener(json);
            Object next = token.nextValue();

            Object o = parse.parseJson(next);

            if (apiId.equals(DEFAUL_API_ID) && o instanceof JSONObject) {
                JSONObject object = (JSONObject) o;
                object.put(apiId, i);
            }

            array.put(o);
        }
        return array;
    }

}
