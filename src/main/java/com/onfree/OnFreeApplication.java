package com.onfree;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class OnFreeApplication {

    public static void main(String[] args) {
        final String APPLICATION_LOCATIONS
                = "spring.config.location = " +"classpath:application.yml, classpath:aws.yml";
        new SpringApplicationBuilder(OnFreeApplication.class)
                .properties(APPLICATION_LOCATIONS)
                .run(args);

    }

}
