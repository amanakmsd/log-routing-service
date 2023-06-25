package com.aman.logroutingservice.producer.queue;

import com.aman.logroutingservice.config.FileConfig;
import com.aman.logroutingservice.consumer.handler.ConsumerHandler;
import com.aman.logroutingservice.model.log.LogRequest;
import com.aman.logroutingservice.model.log.LogResponse;
import com.aman.logroutingservice.repository.file.LogFileRepository;
import com.aman.logroutingservice.utils.MutexUtil;
import com.aman.logroutingservice.utils.QueueUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Component
@Slf4j
public class Consumer {

    @Autowired private LogFileRepository logFileRepository;
    @Autowired private FileConfig fileConfig;
    @Autowired private ConsumerHandler consumerHandler;
    private final ExecutorService executorService;
    Consumer() {
        executorService = Executors.newFixedThreadPool(10);
    }

    @PostConstruct
    public void consumeAsync() {
        log.debug("Consumer | consumeAsync | called");
        Runnable task = this::consume;
        executorService.execute(task);
    }


    private boolean consume() {
        try {
            log.debug("Consumer | consume | started");
            while(true) {
                log.info("Consumer | consume | no. of request pending to process: {}", QueueUtil.size());
                if(!QueueUtil.isEmpty()) {
                    LogRequest logRequest = QueueUtil.peek();
                    LogResponse logResponse = logIt(logRequest);
                    if (logResponse.isStatus()) {
                        QueueUtil.poll();
                    }
                }
            }
        } catch (Exception e) {
            log.error("Consumer | consume | " +
                    "Exception occurred when trying to submit task for migrating {}", e.getMessage());
            return false;
        }
    }

    private LogResponse logIt(LogRequest logRequest) {
        try {

            log.debug("LogService | log | Acquiring mutex for {} {}",
                    logRequest.getSource(), logRequest.getSourceId());
            MutexUtil.mutex.acquire();
            log.debug("LogService | log | Acquired mutex for {} {}",
                    logRequest.getSource(), logRequest.getSourceId());
            //1. Log to buffer
            boolean isLogged = logFileRepository.log(logRequest);
            log.info("LogService | log | Logged to file {} {} {}",
                    logRequest.getSource(), logRequest.getSourceId(), isLogged);
            if (!isLogged) {
                //TODO:- We can further improve this to retry but for sake of simplicity returning false.
                MutexUtil.mutex.release();
                log.warn("LogService | log | Released Mutex | Logged to file failed  {} {}",
                        logRequest.getSource(), logRequest.getSourceId());
                return new LogResponse(false);
            }
            double currentFileSizeInMB = logFileRepository.getFileSize();
            int allowedMaxFileSize = fileConfig.getFileMaxSize();
            //current buffer size vs allowed max buffer size
            if (currentFileSizeInMB > allowedMaxFileSize) {
                log.info("LogService | log | current file size is high {} {}",
                        currentFileSizeInMB, allowedMaxFileSize);
                MutexUtil.mutex.release();
                log.debug("LogService | log | Mutex released {} {}",
                        logRequest.getSource(), logRequest.getSourceId());
                //Requesting consumer to migrate data from buffer
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
