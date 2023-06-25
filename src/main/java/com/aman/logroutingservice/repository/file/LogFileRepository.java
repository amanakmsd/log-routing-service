package com.aman.logroutingservice.repository.file;

import com.aman.logroutingservice.model.log.LogRequest;
import com.aman.logroutingservice.utils.FileUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class LogFileRepository{

    public boolean log(LogRequest logRequest) {
        try {
            File file
                    = new File("/Users/amanakmsd/IdeaProjects" +
                    "/log-routing-service/src/main/resources/temp.txt");
            String logRequestString = new ObjectMapper().writeValueAsString(logRequest);
            FileUtils.writeStringToFile(
                    file, logRequestString + "\n", StandardCharsets.UTF_8, true);
            log.info("LogFileRepository | log | Successfully logged to file {} {}", logRequest.getSource(), logRequest.getSourceId());
            return true;
        } catch (Exception e) {
            log.error("Exception occurred when logging to file {}", e.getMessage());
            return false;
        }
    }

    public double getFileSize() {
        File file = new File("/Users/amanakmsd/IdeaProjects" +
                "/log-routing-service/src/main/resources/temp.txt");
        double fileSizeInMB =  FileUtil.getFileSizeMegaBytes(file);
        log.info("LogFileRepository | getFileSize | File size : {}", fileSizeInMB);
        return fileSizeInMB;
    }

    public List<LogRequest> getFileContent() {
        try {
            File file
                    = new File("/Users/amanakmsd/IdeaProjects" +
                    "/log-routing-service/src/main/resources/temp.txt");
            List<String> logList = FileUtils.readLines(file, StandardCharsets.UTF_8);
            List<LogRequest> logRequestList = new ArrayList<>();
            log.info("LogFileRepository | getFileSize | Number of elements in file : {}", logList.size());
            for (String log : logList) {
                LogRequest logRequest = new ObjectMapper().readValue(log, LogRequest.class);
                logRequestList.add(logRequest);
            }
            return logRequestList;
        } catch (Exception e) {
            log.error("LogFileRepository | getFileSize | Exception occurred when getting file content : {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    public boolean deleteFileContent() {
        try {
            File file = new File("/Users/amanakmsd/IdeaProjects" +
                    "/log-routing-service/src/main/resources/temp.txt");
            FileUtils.write(file, "", StandardCharsets.UTF_8);
            log.info("LogFileRepository | deleteFileContent | Success: {}", true);
            return true;
        } catch (Exception e) {
            log.error("Exception occurred when deleting file content : {}", e.getMessage());
            return false;
        }
    }


}
