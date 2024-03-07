package com.example.gazamung;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@SpringBootApplication
@PropertySources({
        @PropertySource("classpath:application-jwt.properties"),
        @PropertySource("classpath:application-smtp.properties"),
        @PropertySource("classpath:application-database.properties")
})
public class GazamungApplication {

    public static void main(String[] args) {
        SpringApplication.run(GazamungApplication.class, args);
    }

}
