package com.aman.logroutingservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
@Getter
@Setter
public class DBConfig {
    @Value("${db.mysql.url}")
    private String dbUrl;
    @Value("${db.mysql.username}")
    private String mySQLUsername;
    @Value("${db.mysql.password}")
    private String mySQLPassword;
    @Value("${db.mysql.driver-class-name}")
    private String mySQLDriverClassName;
}
