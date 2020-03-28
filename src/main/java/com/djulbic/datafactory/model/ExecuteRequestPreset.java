package com.djulbic.datafactory.model;

import java.util.Objects;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExecuteRequestPreset that = (ExecuteRequestPreset) o;
        return presetName.equals(that.presetName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(presetName);
    }

    @Override
    public String toString() {
        return "ExecuteRequestPreset{" +
                "presetName='" + presetName + '\'' +
                ", request=" + request +
                '}';
    }
}
