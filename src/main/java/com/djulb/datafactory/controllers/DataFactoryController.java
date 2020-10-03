package com.djulb.datafactory.controllers;

import com.djulb.datafactory.controllers.request.PostRequest;
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
            HttpServletRequest request,
            @RequestBody(required = false) String json
    ) throws IOException, InvocationTargetException, IllegalAccessException {
        PostRequest dlReq = new PostRequest(request, json);
        JSONArray parse = (JSONArray) parseJsonString(dlReq.getJson(), 1);
        return new ResponseEntity<>(parse.get(0).toString(), HttpStatus.OK);
    }

    @PostMapping("/{numberOfItems}")
    public ResponseEntity<String> parseRandomObjects(
            HttpServletRequest request,
            @PathVariable(name = "numberOfItems") int numberOfItems,
            @RequestBody(required = false) String json
    ) throws IOException, InvocationTargetException, IllegalAccessException {
        PostRequest dlReq = new PostRequest(request, json);

        JSONArray parse = parseJsonString(dlReq.getJson(),numberOfItems);
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

    @PostMapping("/api/{apiName}")
    public ResponseEntity<String> save(
            HttpServletRequest request,
            @PathVariable(name = "apiName") String apiName,
            @RequestBody(required = false) String json) throws InvocationTargetException, IllegalAccessException {

        PostRequest dlReq = new PostRequest(request, json);

        Api api = apiMap.get(apiName);
        if (api == null) {
            return new ResponseEntity("No api with that name", HttpStatus.BAD_REQUEST);
        }

        JsonParserDL parse = new JsonParserDL(data);
        Object objectForSaving = parse.parseJson(dlReq.getJson());
        Object save = api.save(objectForSaving);

        if (save != null) {
            return new ResponseEntity(save.toString(), HttpStatus.OK);
        }
        return new ResponseEntity("No element with that id", HttpStatus.BAD_REQUEST);
    }

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

        PostRequest dlReq = new PostRequest(request, json);
        JsonParserDL parse = new JsonParserDL(data);
        Object objectForSaving = parse.parseJson(dlReq.getJson());
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

//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        String json = gson.toJson(object);

        JSONArray parse = parseJsonString(object.toString(), apiCount);

        Api api = new Api.Builder()
                .withName(apiName)
                .withWait(apiWait)
                .withData(parse)
                .build();

        apiMap.put(apiName, api);

        return new ResponseEntity<>(parse.toString(), HttpStatus.OK);
    }

    @PostMapping("/set/{apiName}")
    public ResponseEntity<String> getDataList(
            HttpServletRequest request,
            @RequestBody(required = false) String json,
            @PathVariable(name = "apiName") String apiName
    ) throws InvocationTargetException, IllegalAccessException {

        PostRequest dlReq = new PostRequest(request, json);

        JSONArray parse = (JSONArray) parseJsonString(dlReq.getJson(), dlReq.getApiCount());

        Api api = new Api.Builder()
                .withName(apiName)
                .withId(dlReq.getApiId())
                .withWait(dlReq.getApiWait())
                .withData(parse)
                .build();
        apiMap.put(apiName, api);

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
}
