package com.example.zaliczenie_spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ZaliczenieSpringApplication {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BmiRepository bmiRepository;

    public static void main(String[] args) {
        SpringApplication.run(ZaliczenieSpringApplication.class, args);
    }

}
