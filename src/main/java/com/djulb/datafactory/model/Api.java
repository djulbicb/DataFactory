package com.djulb.datafactory.model;

import com.djulb.datafactory.util.Common;
import com.github.rjeschke.txtmark.Run;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public class Api {

    Api() {

    }

    private String id;
    private String name;
    private int wait = 0;
    private JSONArray data = new JSONArray();
    private int count = 0;

    public JSONArray getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Api{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", wait=" + wait +
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


    /////////////////////////////////////////////////
    // CRUD REPOSITORY
    /////////////////////////////////////////////////

    public Iterable findAll() {
        waitBeforeResponding();
        return data;
    }

    public long count() {
        return data.length();
    }

    public Optional<Object> findById(Object searchId) {
        waitBeforeResponding();

        for (Object obj : getData()) {
            if (obj instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) obj;
                Object o = jsonObject.get(getId());

                if (o.toString().equals(searchId)) {
                    return Optional.of(jsonObject);
                }
            } else if (obj instanceof org.json.JSONArray) {
                JSONArray array = (JSONArray) obj;
                int index = Integer.parseInt(searchId.toString());
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

    public Object update(String apiId, Object saveObj) {
        if (saveObj instanceof JSONObject) {
            JSONObject jsonObj = (JSONObject) saveObj;
            if (!jsonObj.has(getId()) && getId().equals(Common.DEFAUL_API_ID)) {
                jsonObj.put(getId(), count);
            } else {
                jsonObj.put(getId(), apiId);
            }
            getData().put(saveObj);
            return saveObj;
        } else if (saveObj instanceof JSONArray) {
            return getData().put(Integer.parseInt(apiId), saveObj);
        } else {
            throw new RuntimeException("Unknown data type for updating");
        }

    }

    public Object save(Object saveObj) {
        if (saveObj instanceof JSONObject) {
            JSONObject jsonObj = (JSONObject) saveObj;
            if (!jsonObj.has(getId()) && getId().equals(Common.DEFAUL_API_ID)) {
                jsonObj.put(getId(), count);
            }
        }
        count++;
        return getData().put(saveObj);
    }

    public boolean deleteById(Object searchId) {
        waitBeforeResponding();
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

    public boolean deleteAll() {
        waitBeforeResponding();
        data = new JSONArray();
        return true;
    }

    /////////////////////////////////////////////////
    // BUILDER
    /////////////////////////////////////////////////

    public static class Builder {

        private Api config;

        public Builder() throws InvocationTargetException, IllegalAccessException {
            config = new Api();
        }
        public Builder withName(String name) {
            this.config.name = name;
            return this;
        }

        public Builder withWait(int wait) {
            this.config.wait = wait;
            return this;
        }

        public Builder withData(JSONArray data) {
            for (Object o : data) {
                this.config.save(o);
            }
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


