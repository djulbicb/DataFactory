package com.djulb.datafactory.model;

public class ApiConfig {
    private String apiId = "apiId";
    private String apiName;
    private int apiWait = 0;
    private int apiCount = 1;

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public int getApiWait() {
        return apiWait;
    }

    public void setApiWait(int apiWait) {
        this.apiWait = apiWait;
    }

    public int getApiCount() {
        return apiCount;
    }

    public void setApiCount(int apiCount) {
        this.apiCount = apiCount;
    }

    public String getApiId() {
        return apiId;
    }

    public void setApiId(String apiId) {
        this.apiId = apiId;
    }

    @Override
    public String toString() {
        return "ApiConfig{" +
                "apiName='" + apiName + '\'' +
                ", apiWait=" + apiWait +
                ", apiCount=" + apiCount +
                ", apiId='" + apiId + '\'' +
                '}';
    }
}
