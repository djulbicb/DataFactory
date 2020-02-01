package com.djulbic.datafactory.configuration;


import data.DataLibrary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
public class BeanConfiguration {

    @Bean
    public DataLibrary dataLibrary(){
        return DataLibrary.getEnglishData();
    }
}
