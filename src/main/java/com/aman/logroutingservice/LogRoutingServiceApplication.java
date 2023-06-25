package com.aman.logroutingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication()
@EnableScheduling
public class LogRoutingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LogRoutingServiceApplication.class, args);
    }

}
