package com.djulbic.datafactory.model;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

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


    /////////////////////////////////////////////////
    // CRUD REPOSITORY
    ////////////////////////////////////////////////

    public Iterable findAll() {
        return data;
    }

    public long count() {
        return data.length();
    }

    public Iterable saveAll(Iterable iterable) {
        return null;
    }

    public Optional<Object> findById(Object searchId) {
        for (Object obj : getData()) {
            if (obj instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) obj;
                Object o = jsonObject.get(getId());

                if (o.toString().equals(searchId)) {
                    waitBeforeResponding();
                    return Optional.of(jsonObject);
                }
            } else if (obj instanceof org.json.JSONArray) {
                JSONArray array = (JSONArray) obj;
                int index = Integer.parseInt(searchId.toString());
                waitBeforeResponding();
                return  Optional.of(array.get(index));
            }
        }

        return Optional.empty();
    }

    private void waitBeforeResponding() {
        if (wait > 0) {
            try {
                Thread.sleep(wait);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean existsById(Object searchId) {
        return findById(searchId).isPresent();
    }

    public Object save(Object o) {
        return null;
    }

    public boolean deleteById(Object searchId) {
        for (int i = 0; i < getData().length(); i++) {
            Object obj = getData().get(i);

            if (obj instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) obj;
                Object o = jsonObject.get(getId());

                if (o.toString().equals(searchId)) {
                    getData().remove(i);
                    return true;
                }
            } else if (obj instanceof org.json.JSONArray) {
                JSONArray array = (JSONArray) obj;
                int index = Integer.parseInt(searchId.toString());
                array.remove(index);
                return true;
            }
        }

        return false;
    }

    public void deleteAll() {
        data = new JSONArray();
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


