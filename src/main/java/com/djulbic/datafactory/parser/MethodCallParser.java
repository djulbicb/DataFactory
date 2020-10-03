package com.djulbic.datafactory.parser;

import data.DataLibrary;

import javax.el.MethodNotFoundException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class MethodCallParser {

    public Object parse(DataLibrary dataLibrary, String methodCall, String delimiter){
        String methodNameSubstr = getMethodNameSubstrFromString(methodCall);
        String paramSubstr = getMethodParamsSubstrFromString(methodCall);
        List<String> extractedParams = getParametarExtracted(paramSubstr, delimiter);

        System.out.println(methodNameSubstr);
        System.out.println(extractedParams);

        Method methodByName = getMethodByName(methodNameSubstr, DataLibrary.class);
        Object[] params = getParams(methodByName, extractedParams);

        System.out.println(Arrays.toString(params));

        try {
            Object invoke = methodByName.invoke(dataLibrary, params);
            System.out.println("Return: " + invoke.toString());
            return invoke;

        }catch (Exception e){
            throw new MethodNotFoundException(methodCall);
        }
    }

    public Method getMethodByName(String methodName, Class scanClass){
        List<Method> methods = new ArrayList<>();
        Method[] declaredMethods = scanClass.getDeclaredMethods();
        for (Method method : declaredMethods) {
            if (method.getName().equalsIgnoreCase(methodName)){
                return method;
            }
        }
        throw new MethodNotFoundException("Method " + methodName + " not found");
    }

    public String getMethodNameSubstrFromString(String methodCall){
        if (methodCall.contains("(") && methodCall.contains(")")){
            String methondName = methodCall.substring(0, methodCall.indexOf("("));
            return methondName.trim();
        }
        return methodCall;
    }

    public String getMethodParamsSubstrFromString(String methodCall){
        if (methodCall.contains("(") && methodCall.contains(")")){
            methodCall = methodCall.substring(methodCall.indexOf("(")+1, methodCall.lastIndexOf(")") );
            return methodCall.trim();
        }
        return methodCall;
    }

    public List<String> getParametarExtracted(String paramsAsString,  String delimeter){
        List<String> tokensList = new ArrayList<String>();

        String[] split =paramsAsString.split(Pattern.quote(delimeter));
        List<String> strings = Arrays.asList(split);
        return strings;
    }

    public Object[] getParams(Method method, List<String> extractedParams){
        System.out.println(method.getName());
        System.out.println(method.getParameterCount());

        int parametarCount = method.getParameterCount();
        Object[] methodParams = new Object[parametarCount];
        if (parametarCount > 0){

            for (int i = 0; i < method.getParameters().length; i++) {
                Parameter parameter = method.getParameters()[i];
                Class<?> type = parameter.getType();

                if (type == int.class){
                    System.out.println("type - int");
                    System.out.println(extractedParams.get(i));
                    methodParams[i] = Integer.parseInt(extractedParams.get(i));
                }else if(type == float.class){
                    System.out.println("type - float");
                    methodParams[i] = Float.parseFloat(extractedParams.get(i));
                }else if(type == double.class){
                    System.out.println("type - double");
                    methodParams[i] = Double.parseDouble(extractedParams.get(i));
                } else if (type == String.class){
                    System.out.println("type - String");
                    String s = extractedParams.get(i);

                    methodParams[i] = s; //extractedParams.get(i);
                } else if(parameter.isVarArgs()){
                    System.out.println("type - var args");
                    methodParams[i] = extractedParams.toArray();
                } else{
                    System.out.println("type - not find");
                }
            }

        }
        for (Parameter parameter : method.getParameters()) {
            System.out.println(parameter.getType());
        }
        System.out.println("---");
        return methodParams;
    }


}
