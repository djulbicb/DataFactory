package com.djulbic.datafactory.model;

public class ExecuteRequestPreset {
    String presetName;
    ExecuteRequestDTO request;

    public String getPresetName() {
        return presetName;
    }

    public void setPresetName(String presetName) {
        this.presetName = presetName;
    }

    public ExecuteRequestDTO getRequest() {
        return request;
    }

    public void setRequest(ExecuteRequestDTO request) {
        this.request = request;
    }

    @Override
    public String toString() {
        return "ExecuteRequestPreset{" +
                "presetName='" + presetName + '\'' +
                ", request=" + request +
                '}';
    }
}
