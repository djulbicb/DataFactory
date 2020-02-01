package com.djulbic.datafactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import static org.springframework.boot.SpringApplication.run;


@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class DatafactoryApplication {

	public static void main(String[] args) {
		run(DatafactoryApplication.class, args);
	}

}
