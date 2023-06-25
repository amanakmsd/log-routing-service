package com.aman.logroutingservice.repository.db;

import com.aman.logroutingservice.model.log.LogRequest;
import com.aman.logroutingservice.utils.DBUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

@Component
@Slf4j
public class LogDBRepository {

    @Autowired private DBUtil dbUtil;


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
