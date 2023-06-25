package com.aman.logroutingservice.utils;

import com.aman.logroutingservice.config.DBConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;

@Component
@Slf4j
public class DBUtil {

    @Autowired private DBConfig dbConfig;

    private Connection conn;

    public Connection getConnection() {
        try {
            if (conn == null || conn.isClosed() || !conn.isValid(0)) {
                //create a new connection
                log.info("DBUtil| getConnection | connection is not valid | Creating connection");
                return createNewConnection();
            }
            log.info("DBUtil| getConnection | Connection is  valid | Returning connection");
            return conn;
        } catch (Exception e) {
            log.error("DBUtil| getConnection | Exception occurred when getting connection {} | Creating connection", e.getMessage());
            return createNewConnection();
        }
    }

    private Connection createNewConnection() {
        try {
            log.info("DBUtil| createNewConnection | Creating connection....");
            String myDriver = dbConfig.getMySQLDriverClassName();
            String myUrl = dbConfig.getMySQLUrl();
            Class.forName(myDriver);
            conn = DriverManager.getConnection(myUrl, dbConfig.getMySQLUsername()
                    , dbConfig.getMySQLPassword());
            return conn;
        } catch (Exception e) {
            log.error("DBUtil| createNewConnection | Exception occurred when creating new connection {}", e.getMessage());
            return null;
        }

    }

}
