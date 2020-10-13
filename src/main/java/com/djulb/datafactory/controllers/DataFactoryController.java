package com.djulb.datafactory.controllers;

import com.djulb.datafactory.model.ApiConfig;
import com.djulb.datafactory.model.Api;
import com.djulb.datafactory.parser.JsonParserDL;
import com.google.gson.*;
import data.DataLibrary;
import org.json.JSONArray;
import org.markdown4j.Markdown4jProcessor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static com.djulb.datafactory.util.Common.RESERVED_WORDS;

@Controller
public class DataFactoryController {
    private Map<String, Api> apiMap = new HashMap();
    DataLibrary data = DataLibrary.getEnglishData();

    /////////////////////////////////////////////////
    // WELCOME PAGE
    /////////////////////////////////////////////////

    @GetMapping("/")
    public String showWelcomeScreen1(Model model) throws IOException {
        String html = new Markdown4jProcessor().process(readResourceAsStream("classpath:/README.md"));
        model.addAttribute("content", html);

        return "index";
    }

    /////////////////////////////////////////////////
    // BASIC RANDOM GENERATOR
    /////////////////////////////////////////////////

    @PostMapping("/")
    public ResponseEntity<String> parseRandomObject(
            @RequestBody(required = false) String json
    ) throws IOException, InvocationTargetException, IllegalAccessException {
        JSONArray parse = parseJsonString(json, 1);
        return new ResponseEntity<>(parse.get(0).toString(), HttpStatus.OK);
    }

    @PostMapping("/{numberOfItems}")
    public ResponseEntity<String> parseRandomObjects(
            @PathVariable(name = "numberOfItems") int numberOfItems,
            @RequestBody(required = false) String json
    ) throws IOException, InvocationTargetException, IllegalAccessException {
        JSONArray parse = parseJsonString(json, numberOfItems);
        return new ResponseEntity<>(parse.toString(), HttpStatus.OK);
    }

    /////////////////////////////////////////////////
    // GET
    /////////////////////////////////////////////////

    @GetMapping("/api/{apiName}")
    public ResponseEntity<String> get(@PathVariable(name = "apiName") String apiName) {
        Api api = apiMap.get(apiName);
        if (api == null) {
            return new ResponseEntity("No api with that name", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(api.findAll().toString(), HttpStatus.OK);
    }

    @GetMapping("/api/{apiName}/{apiId}")
    public ResponseEntity<String> getOne(
            @PathVariable(name = "apiName") String apiName,
            @PathVariable(name = "apiId") String apiId) {
        Api api = apiMap.get(apiName);
        if (api == null) {
            return new ResponseEntity("No api with that name", HttpStatus.BAD_REQUEST);
        }

        Optional byId = api.findById(apiId);
        if (byId.isPresent()) {
            return ResponseEntity.ok().body(byId.get().toString());
        }
        return new ResponseEntity("No element with that id", HttpStatus.BAD_REQUEST);
    }

    /////////////////////////////////////////////////
    // SAVE / UPDATE
    /////////////////////////////////////////////////

    @PostMapping("/api/{apiName}/{apiId}")
    public ResponseEntity<String> update(
            HttpServletRequest request,
            @PathVariable(name = "apiName") String apiName,
            @PathVariable(name = "apiId") String apiId,
            @RequestBody(required = false) String json) throws InvocationTargetException, IllegalAccessException {
        Api api = apiMap.get(apiName);

        if (api == null) {
            return new ResponseEntity("No api with that name", HttpStatus.BAD_REQUEST);
        }

        JsonParserDL parse = new JsonParserDL(data);
        Object objectForSaving = parse.parseJson(json);
        Object save = api.update(apiId, objectForSaving);

        if (save != null) {
            return new ResponseEntity(save.toString(), HttpStatus.OK);
        }
        return new ResponseEntity("No element with that id", HttpStatus.BAD_REQUEST);
    }

    /////////////////////////////////////////////////
    // DELETE
    /////////////////////////////////////////////////

    @DeleteMapping("/api/{apiName}")
    public ResponseEntity<String> delete(
            @PathVariable(name = "apiName") String apiName) {

        Api api = apiMap.remove(apiName);
        if (api != null) {
            return new ResponseEntity(String.format("Api %s removed", api.getName()), HttpStatus.OK);
        }
        return new ResponseEntity("No api with that name", HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/api/{apiName}/{apiId}")
    public ResponseEntity<String> deleteOne(
            @PathVariable(name = "apiName") String apiName,
            @PathVariable(name = "apiId") String apiId) {
        Api api = apiMap.get(apiName);
        if (api == null) {
            return new ResponseEntity("No api with that name", HttpStatus.BAD_REQUEST);
        }

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
    public ResponseEntity<String> set(
            HttpServletRequest request,
            @PathVariable String apiName,
            ApiConfig apiConfig) throws InvocationTargetException, IllegalAccessException {

        JsonObject object = buildJsonObjFromGetRequest(request);
        JSONArray parse = parseJsonString(object.toString(), apiConfig.getApiCount());

        Api api = new Api.Builder()
                .withId(apiConfig.getApiId())
                .withName(apiName)
                .withWait(apiConfig.getApiWait())
                .withData(parse)
                .build();
        apiMap.put(apiConfig.getApiName(), api);

        return new ResponseEntity<>(parse.toString(), HttpStatus.OK);
    }

    @PostMapping("/set/{apiNameVar}")
    public ResponseEntity<String> getDataList(
            HttpServletRequest request,
            @RequestBody(required = false) String json,
            @PathVariable(name = "apiNameVar") String apiNameVar,
            ApiConfig apiConfig
    ) throws InvocationTargetException, IllegalAccessException {
        JSONArray parse = parseJsonString(json, apiConfig.getApiCount());

        Api api = new Api.Builder()
                .withName(apiNameVar)
                .withId(apiConfig.getApiId())
                .withWait(apiConfig.getApiWait())
                .withData(parse)
                .build();
        apiMap.put(apiNameVar, api);

        return new ResponseEntity(parse.toString(), HttpStatus.OK);
    }

    /////////////////////////////////////////////////
    // FUNCTIONS
    /////////////////////////////////////////////////

    private Object parseJsonString(String json) throws InvocationTargetException, IllegalAccessException {
        JsonParserDL parse = new JsonParserDL(data);
        return parse.parseJson(json);
    }

    private JSONArray parseJsonString(String json, int count) throws InvocationTargetException, IllegalAccessException {
        JsonParserDL parse = new JsonParserDL(data);
        JSONArray array = new JSONArray();

        for (int i = 0; i < count; i++) {
            Object o = parse.parseJson(json);
            array.put(o);
        }
        return array;
    }

    private InputStream readResourceAsStream(String s) throws IOException {
        ClassLoader cl = this.getClass().getClassLoader();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(cl);
        Resource resource = resolver.getResource("classpath:/README.md");
        return resource.getInputStream();
    }

    /**
     * Ignores url request params that configure rest service like apiWait, apiName...,
     * and creates a basic json object from the rest of url params
     * @param request
     * @return
     */
    private JsonObject buildJsonObjFromGetRequest(HttpServletRequest request) {
        JsonObject jsonObject = new JsonObject();

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

            jsonObject.addProperty(key, value);
        }
        return jsonObject;
    }

}
