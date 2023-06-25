package com.aman.logroutingservice.repository.db;

import com.aman.logroutingservice.model.log.LogRequest;
import com.aman.logroutingservice.utils.DBUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Component
@Slf4j
public class LogDBRepository {

    @Autowired private DBUtil dbUtil;

    public boolean createLogTable() {
        try {
            Connection conn = dbUtil.getConnection();
            Statement statement = conn.createStatement();
            String createTableQuery = "create table if not exists log_table(ID int NOT NULL " +
                    "auto_increment primary key, EVENT_NAME varchar(255), TIMESTAMP BIGINT," +
                    " SOURCE varchar(255), SOURCE_ID varchar(255), LOG_MESSAGE TEXT);";
            statement.execute(createTableQuery);
            log.info("LogDBRepository | createLogTable | successfully created log table");
            return true;
        } catch (Exception e) {
            log.error("LogDBRepository | createLogTable | Exception occurred when creating log table {}"
                    , e.getMessage());
            return false;
        }
    }


    public boolean batchLog(List<LogRequest> logRequestList) {
        try {
            Connection conn = dbUtil.getConnection();
            String insertQuery = "INSERT INTO log_table(EVENT_NAME,TIMESTAMP,SOURCE,SOURCE_ID,LOG_MESSAGE) values(?,?,?,?,?)";
            PreparedStatement preparedStmt = conn.prepareStatement(insertQuery);
            for (LogRequest logRequest : logRequestList) {
                preparedStmt.setString(1, logRequest.getEventName());
                preparedStmt.setLong(2, logRequest.getTimeStamp());
                preparedStmt.setString(3, logRequest.getSource());
                preparedStmt.setString(4, logRequest.getSourceId());
                preparedStmt.setString(5, logRequest.getLogMessage());
                preparedStmt.addBatch();
            }
            preparedStmt.executeBatch();
            return true;
        } catch (Exception e) {
            log.error("LogDBRepository | batchLog | Exception occurred when logging batch items : {}", e.getMessage());
            return false;
    }
    }


}
