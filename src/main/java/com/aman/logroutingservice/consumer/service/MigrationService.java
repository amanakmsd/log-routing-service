package com.aman.logroutingservice.consumer.service;

import com.aman.logroutingservice.model.log.LogRequest;
import com.aman.logroutingservice.model.migration.MigrationResponse;
import com.aman.logroutingservice.repository.db.LogDBRepository;
import com.aman.logroutingservice.repository.file.LogFileRepository;
import com.aman.logroutingservice.utils.MutexUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class MigrationService {

    @Autowired private LogFileRepository logFileRepository;
    @Autowired private LogDBRepository logDBRepository;


    @Scheduled(fixedDelay = 30000)
    public MigrationResponse migrate() {
        try {
            log.info("MigrationService | migrate | Acquiring mutex");
            MutexUtil.mutex.acquire();
            log.info("MigrationService | migrate | Acquired mutex");
            List<LogRequest> logRequestList = logFileRepository.getFileContent();
            log.info("MigrationService | migrate | Log request size : {}", logRequestList.size());
            boolean isLoggedSuccess = logDBRepository.batchLog(logRequestList);
            log.info("MigrationService | migrate | Batch logged to db : {}", isLoggedSuccess);
            if (isLoggedSuccess) {
                //delete contents of file
                log.info("MigrationService | migrate | Deleting contents of file");
                logFileRepository.deleteFileContent();
            }
            log.info("MigrationService | migrate | Releasing mutex");
            MutexUtil.mutex.release();
            log.info("MigrationService | migrate | Released mutex successfully");
            return new MigrationResponse(isLoggedSuccess);
        } catch (Exception e) {
            log.error("MigrationService | migrate | Exception occurred when migrating file contents to db : {}"
                    , e.getMessage());
            MutexUtil.mutex.release();
            log.debug("MigrationService | migrate | released mutex in catch block");
            return new MigrationResponse(false);
        }
    }
}
