package com.djulbic.datafactory;

import data.DataLibrary;

import javax.el.MethodNotFoundException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class DataLibraryMethodCallParser implements MethodCallParser{
    @Override
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


}
