package com.djulbic.datafactory;

import data.DataLibrary;

import javax.el.MethodNotFoundException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public interface MethodCallParser {
    Object parse(DataLibrary dataLibrary, String m);

    default Method getMethodByName(String methodName, Class scanClass){
        List<Method> methods = new ArrayList<>();
        Method[] declaredMethods = scanClass.getDeclaredMethods();
        for (Method method : declaredMethods) {
            if (method.getName().equalsIgnoreCase(methodName)){
                return method;
            }
        }
        throw new MethodNotFoundException("Method " + methodName + " not found");
    }

    default String getMethodNameSubstrFromString(String methodCall){
        if (methodCall.contains("(") && methodCall.contains(")")){
            String methondName = methodCall.substring(0, methodCall.indexOf("("));
            return methondName.trim();
        }
        return methodCall;
    }

    default String getMethodParamsSubstrFromString(String methodCall){
        if (methodCall.contains("(") && methodCall.contains(")")){
            methodCall = methodCall.substring(methodCall.indexOf("(")+1, methodCall.lastIndexOf(")") );
            return methodCall.trim();
        }
        return methodCall;
    }

    default List<String> getParametarExtracted(String tested){
        List<String> tokensList = new ArrayList<String>();
        boolean inQuotes = false;
        StringBuilder b = new StringBuilder();
        for (char c : tested.toCharArray()) {
            switch (c) {
                case ',':
                    if (inQuotes) {
                        b.append(c);
                    } else {
                        tokensList.add(b.toString().trim());
                        b = new StringBuilder();
                    }
                    break;
                case '\"':
                    inQuotes = !inQuotes;
                default:
                    b.append(c);
                    break;
            }
        }
        tokensList.add(b.toString().trim());
        return tokensList;
    }

    default Object[] getParams(Method method, List<String> extractedParams){
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
                    methodParams[i] = extractedParams.get(i);
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
