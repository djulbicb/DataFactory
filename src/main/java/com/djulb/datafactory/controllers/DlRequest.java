package com.djulb.datafactory.controllers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.servlet.http.HttpServletRequest;

import static com.djulb.datafactory.util.Common.DEFAULT_API_ITEM_COUNT;
import static com.djulb.datafactory.util.Common.DEFAULT_API_WAIT_TIME;
import static com.djulb.datafactory.util.Common.DEFAUL_API_ID;
import static com.djulb.datafactory.util.Common.RESERVED_WORDS;

public class DlRequest {

    private String json;
    private int apiWait;
    private int apiCount;
    private String apiId;

    public DlRequest(HttpServletRequest request, String json) {
        this.json = json;

        apiWait = DEFAULT_API_WAIT_TIME;
        apiCount = DEFAULT_API_ITEM_COUNT;
        apiId = DEFAUL_API_ID;

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
            this.json = elementObj.toString();
        }


    }

    public String getJson() {
        return json;
    }

    public int getApiWait() {
        return apiWait;
    }

    public int getApiCount() {
        return apiCount;
    }

    public String getApiId() {
        return apiId;
    }
}
