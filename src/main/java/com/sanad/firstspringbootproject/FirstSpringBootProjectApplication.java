package com.sanad.firstspringbootproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


//This stores application configuration
@SpringBootApplication /* Tells spring boot to treat this as a configuration class and to enable automatic config and to scan
this package and its child packages*/
public class FirstSpringBootProjectApplication {

    public static void main(String[] args) {

        SpringApplication.run(FirstSpringBootProjectApplication.class, args);
    }

}
