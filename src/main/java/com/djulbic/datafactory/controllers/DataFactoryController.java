package com.djulbic.datafactory.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import data.DataLibrary;
import org.json.JSONArray;
import org.json.JSONTokener;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class DataFactoryController {

    private Map<String, JsonElement> apiMap = new HashMap();

    @GetMapping("/{command}")
    public ResponseEntity<String> test (@PathVariable("command") String command) {
        System.out.println(command);

        return ResponseEntity.ok("sss");
    }

    @GetMapping("/set")
    public ResponseEntity<String> set (HttpServletRequest request) {
        Map<String, String[]> map = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : map.entrySet()) {
            System.out.println(entry);
        }


        return ResponseEntity.ok("sss");
    }

    @PostMapping(value = "/getdata/{x}")
    public String getDataList(
            @RequestBody(required = false) String json,
            @PathVariable("x") int count,
            @RequestParam(required = false, defaultValue = "ENGLISH") String language) throws InvocationTargetException, IllegalAccessException {

        DataLibrary data = DataLibrary.getEnglishData();

        JsonParse parse = new JsonParse(data);
        List<Object> response = new ArrayList<>();
        JSONArray array = new JSONArray();

        for (int i = 0; i < count; i++) {
            JSONTokener token = new JSONTokener(json);
            Object next = token.nextValue();
            Object o = parse.parseJson(next);
            //response.add(o);
            array.put(o);
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonElement jsonElement = gson.toJsonTree(response);

        //return gson.toJson(response);


        return array.toString();
    }

}
