package com.djulbic.datafactory.model;

import org.json.JSONArray;

import java.lang.reflect.InvocationTargetException;

public class Api {
    private String id;

    Api() {

    }

    private String name;
    private int wait;
    private String json;
    private JSONArray data;

    public JSONArray getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Api{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", wait=" + wait +
                ", json='" + json + '\'' +
                ", data=" + data +
                '}';
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getWait() {
        return wait;
    }

    public String getJson() {
        return json;
    }

    public static class Builder {

        private Api config;

        public Builder() throws InvocationTargetException, IllegalAccessException {
            config = new Api();

            config.wait = 0;
            config.json = "{}";
        }
        public Builder withName(String name) {
            this.config.name = name;
            return this;
        }

        public Builder withWait(int wait) {
            this.config.wait = wait;
            return this;
        }

        public Builder withJson(String json) {
            this.config.json = json;
            return this;
        }

        public Builder withData(JSONArray data) {
            this.config.data = data;
            return this;
        }

        public Api build () {
            return config;
        }


        public Builder withId(String apiId) {
            this.config.id = apiId;
            return this;
        }
    }
}


