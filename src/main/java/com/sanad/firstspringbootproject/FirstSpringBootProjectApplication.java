package com.sanad.firstspringbootproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
@EnableJms
public class FirstSpringBootProjectApplication {

    static void main(String[] args) {
        SpringApplication.run(FirstSpringBootProjectApplication.class, args);
    }

}
