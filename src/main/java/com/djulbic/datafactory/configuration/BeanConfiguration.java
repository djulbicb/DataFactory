package com.djulbic.datafactory.configuration;


import com.djulbic.datafactory.metadata.providers.MySQLMetadataProvider;
import data.DataLibrary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class BeanConfiguration implements WebMvcConfigurer {

    @Bean
    public DataLibrary dataLibrary(){
        return DataLibrary.getEnglishData();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**");
    }


   @Bean
    public MySQLMetadataProvider database(){
       DataLibrary data = DataLibrary.getEnglishData();
       String connectionUrl = "jdbc:mysql://localhost:3306";
       String username = "root";
       String password = "";
       MySQLMetadataProvider mysqlProvider = new MySQLMetadataProvider(connectionUrl, username, password);
       return mysqlProvider;
   }

}
