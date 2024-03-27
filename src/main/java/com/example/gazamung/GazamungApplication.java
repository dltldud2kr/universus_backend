package com.example.gazamung;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication

public class GazamungApplication {
    public static void main(String[] args) {
        SpringApplication.run(GazamungApplication.class, args);
    }

}
