package com.aman.logroutingservice.producer.service;

import com.aman.logroutingservice.config.FileConfig;
import com.aman.logroutingservice.consumer.handler.ConsumerHandler;
import com.aman.logroutingservice.model.log.LogRequest;
import com.aman.logroutingservice.model.log.LogResponse;
import com.aman.logroutingservice.repository.file.LogFileRepository;
import com.aman.logroutingservice.utils.MutexUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class LogService {

    @Autowired private FileConfig fileConfig;

    @Autowired private LogFileRepository logFileRepository;

    @Autowired private ConsumerHandler consumerHandler;


    public LogResponse log(LogRequest logRequest) {
        try {
            log.info("LogService | log | Acquiring mutex for {} {}",
                    logRequest.getSource(), logRequest.getSourceId());
            MutexUtil.mutex.acquire();
            log.info("LogService | log | Acquired mutex for {} {}",
                    logRequest.getSource(), logRequest.getSourceId());
            boolean isLogged = logFileRepository.log(logRequest);
            log.info("LogService | log | Logged to file {} {} {}",
                    logRequest.getSource(), logRequest.getSourceId(), isLogged);
            if (!isLogged) {
                //TODO:- We can further improve this to retry but for sake of simplicity returning false.
                MutexUtil.mutex.release();
                log.info("LogService | log | Released Mutex | Logged to file failed  {} {}",
                        logRequest.getSource(), logRequest.getSourceId());
                return new LogResponse(false);
            }
            double currentFileSizeInMB = logFileRepository.getFileSize();
            int allowedMaxFileSize = fileConfig.getFileMaxSize();
            if (currentFileSizeInMB > allowedMaxFileSize) {
                log.info("LogService | log | current file size is high {} {}",
                        currentFileSizeInMB, allowedMaxFileSize);
                MutexUtil.mutex.release();
                log.debug("LogService | log | Mutex released {} {}",
                        logRequest.getSource(), logRequest.getSourceId());
                //Requesting consumer to handle the next steps...
                return new LogResponse(consumerHandler.handle());
            }
            MutexUtil.mutex.release();
            log.debug("LogService | log | Mutex released and returning success {} {}",
                    logRequest.getSource(), logRequest.getSourceId());
            return new LogResponse(true);
        } catch (Exception e) {
            log.error("Exception occurred when log : {}", e.getMessage());
            MutexUtil.mutex.release();
            return new LogResponse(false);
        }
    }

}
