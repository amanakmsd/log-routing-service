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


    private void consume() {
        try {
            log.debug("Consumer | consume | started");
            //Retry for 3 times...else ignore
            int retry = 0;
            while(true) {
                if(!QueueUtil.isEmpty()) {
                    log.info("Consumer | consume | Found a request in queue | Size : {}", QueueUtil.size());
                    LogRequest logRequest = QueueUtil.peek();
                    LogResponse logResponse = log(logRequest);
                    if (logResponse.isStatus() || retry == 3) {
                        QueueUtil.poll();
                        retry=0;
                    } else {
                        retry++;
                    }
                }
            }
        } catch (Exception e) {
            log.error("Consumer | consume | " +
                    "Exception occurred when trying to submit task for migrating {}", e.getMessage());
            consumeAsync();
        }
    }

    private LogResponse log(LogRequest logRequest) {
        try {

            log.debug("Consumer | log | Acquiring mutex for {} {}",
                    logRequest.getSource(), logRequest.getSourceId());
            MutexUtil.mutex.acquire();
            log.debug("Consumer | log | Acquired mutex for {} {}",
                    logRequest.getSource(), logRequest.getSourceId());
            //1. Log to buffer
            boolean isLogged = logFileRepository.log(logRequest);
            log.info("Consumer | log | Logged to file {} {} {}",
                    logRequest.getSource(), logRequest.getSourceId(), isLogged);
            if (!isLogged) {
                //TODO:- We can further improve this to retry but for sake of simplicity returning false.
                MutexUtil.mutex.release();
                log.warn("Consumer | log | Released Mutex | Logged to file failed  {} {}",
                        logRequest.getSource(), logRequest.getSourceId());
                return new LogResponse(false);
            }
            double currentFileSizeInMB = logFileRepository.getFileSize();
            int allowedMaxFileSize = fileConfig.getFileMaxSize();
            //current buffer size vs allowed max buffer size
            if (currentFileSizeInMB > allowedMaxFileSize) {
                log.info("Consumer | log | current file size is high {} {}",
                        currentFileSizeInMB, allowedMaxFileSize);
                MutexUtil.mutex.release();
                log.debug("Consumer | log | Mutex released {} {}",
                        logRequest.getSource(), logRequest.getSourceId());
                //Requesting consumer to migrate data from buffer
                return new LogResponse(consumerHandler.handle());
            }
            MutexUtil.mutex.release();
            log.debug("Consumer | log | Mutex released and returning success {} {}",
                    logRequest.getSource(), logRequest.getSourceId());
            return new LogResponse(true);
        } catch (Exception e) {
            log.error("Exception occurred when log : {}", e.getMessage());
            MutexUtil.mutex.release();
            return new LogResponse(false);
        }
    }
}
