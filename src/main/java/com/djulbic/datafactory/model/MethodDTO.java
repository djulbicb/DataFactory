package com.djulbic.datafactory.model;

public class MethodDTO {
    private String methodName;
    private boolean isVarArgs;
    private int paramsCount;

    private String inputParametars;
    private String inputDelimiter;

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public boolean isVarArgs() {
        return isVarArgs;
    }

    public void setVarArgs(boolean varArgs) {
        isVarArgs = varArgs;
    }

    public int getParamsCount() {
        return paramsCount;
    }

    public void setParamsCount(int paramsCount) {
        this.paramsCount = paramsCount;
    }

    public String getInputParametars() {
        return inputParametars;
    }

    public void setInputParametars(String inputParametars) {
        this.inputParametars = inputParametars;
    }

    public String getInputDelimiter() {
        return inputDelimiter;
    }

    public void setInputDelimiter(String inputDelimiter) {
        this.inputDelimiter = inputDelimiter;
    }

    @Override
    public String toString() {
        return "MethodDTO{" +
                "methodName='" + methodName + '\'' +
                ", isVarArgs=" + isVarArgs +
                ", paramsCount=" + paramsCount +
                ", inputParametars='" + inputParametars + '\'' +
                ", inputDelimiter='" + inputDelimiter + '\'' +
                '}';
    }
}
