package com.djulb.datafactory.controllers;

import com.djulb.datafactory.model.Api;
import com.djulb.datafactory.model.JsonParserDL;
import com.djulb.datafactory.util.Utils;
import com.google.gson.*;
import data.DataLibrary;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.JSONArray;
import org.markdown4j.Markdown4jProcessor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Controller
public class DataFactoryController {

    private static final String DEFAUL_API_ID = "apiId";
    private static final int DEFAULT_API_ITEM_COUNT = 1;
    private static final int DEFAULT_API_WAIT_TIME = 0;
    private static final String DEFAULT_API_LANGUAGE = "english";

    private static final List<String> RESERVED_WORDS = Arrays.asList("apiName", "apiWait", "apiCount", "apiId", "apiLanguage");

    private Map<String, Api> apiMap = new HashMap();

    /////////////////////////////////////////////////
    // INDEX
    /////////////////////////////////////////////////

    @GetMapping("/")
    public ResponseEntity<String> showWelcomeScreen1() throws IOException {
        ClassLoader cl = this.getClass().getClassLoader();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(cl);
        Resource resource = resolver.getResource("classpath:/README.md") ;

        // String content = Utils.readLineByLineJava8(new File("classpath:/README.md").getAbsolutePath());
        String html = new Markdown4jProcessor().process(resource.getInputStream());
        return new ResponseEntity<>(html, HttpStatus.OK);
    }

    @PostMapping("/get/{numberOfItems}")
    public ResponseEntity<String> parseRandomObject (
            @PathVariable(name = "numberOfItems") int numberOfItems,
            @RequestBody(required = false) String json
    ) throws IOException, InvocationTargetException, IllegalAccessException {
        JSONArray parse = parseJsonString(json, DEFAUL_API_ID, numberOfItems);
        return new ResponseEntity<>(parse.toString(), HttpStatus.OK);
    }

    /////////////////////////////////////////////////
    // GET
    /////////////////////////////////////////////////

    @GetMapping("/api/{apiName}")
    public ResponseEntity<String> get ( @PathVariable(name = "apiName") String apiName) {
        Api api = apiMap.get(apiName);
        if (api == null) {
            return new ResponseEntity("No api with that name", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(api.findAll().toString(), HttpStatus.OK);
    }

    @GetMapping("/api/{apiName}/{apiId}")
    public ResponseEntity<String> getOne (
            @PathVariable(name = "apiName") String apiName,
            @PathVariable(name = "apiId") String apiId) {
        Api api = apiMap.get(apiName);

        Optional byId = api.findById(apiId);
        if (byId.isPresent()) {
            return ResponseEntity.ok().body(byId.get().toString());
        }
        return new ResponseEntity("No element with that id", HttpStatus.BAD_REQUEST);
    }

    /////////////////////////////////////////////////
    // SAVE
    /////////////////////////////////////////////////

    @PostMapping("/api/{apiName}/{apiId}")
    public ResponseEntity<String> save (
            @PathVariable(name = "apiName") String apiName,
            @PathVariable(name = "apiId") String apiId) {

        return new ResponseEntity("No element with that id", HttpStatus.BAD_REQUEST);
    }

    /////////////////////////////////////////////////
    // UPDATE
    /////////////////////////////////////////////////

    @PutMapping("/api/{apiName}/{apiId}")
    public ResponseEntity<String> update (
            @PathVariable(name = "apiName") String apiName,
            @PathVariable(name = "apiId") String apiId) {

        return new ResponseEntity("No element with that id", HttpStatus.BAD_REQUEST);
    }

    /////////////////////////////////////////////////
    // DELETE
    /////////////////////////////////////////////////

    @DeleteMapping("/api/{apiName}")
    public ResponseEntity<String> delete (
            @PathVariable(name = "apiName") String apiName) {

        Api api = apiMap.remove(apiName);
        if (api != null) {
            return new ResponseEntity(String.format("Api %s removed", api.getName()), HttpStatus.OK);
        }
        return new ResponseEntity("No api with that name", HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/api/{apiName}/{apiId}")
    public ResponseEntity<String> deleteOne (
            @PathVariable(name = "apiName") String apiName,
            @PathVariable(name = "apiId") String apiId) {
        Api api = apiMap.get(apiName);
        boolean deleted = api.deleteById(apiId);
        if (deleted) {
            return new ResponseEntity(api.findAll().toString(), HttpStatus.OK);
        }
        return new ResponseEntity("No element with that id", HttpStatus.BAD_REQUEST);
    }

    /////////////////////////////////////////////////
    // CREATE API DEFINITIONS
    /////////////////////////////////////////////////

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
        JSONArray parse = parseJsonString(json, DEFAUL_API_ID, apiCount);

        Api api = new Api.Builder()
                .withName(apiName)
                .withWait(apiWait)
                .withData(parse)
                .build();

        apiMap.put(apiName, api);

        return new ResponseEntity<>(json, HttpStatus.OK);
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

        JSONArray parse = parseJsonString(jsonForParsing, apiId, apiCount);

        Api api = new Api.Builder()
                .withName(apiName)
                .withId(apiId)
                .withWait(apiWait)
                .withData(parse)
                .build();
        apiMap.put(apiName, api);

        return new ResponseEntity(parse.toString(), HttpStatus.OK);
    }

    /////////////////////////////////////////////////
    // FUNCTIONS
    /////////////////////////////////////////////////

    private JSONArray parseJsonString (String json, String apiId, int count) throws InvocationTargetException, IllegalAccessException {
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
