package com.aman.logroutingservice.consumer.handler;

import com.aman.logroutingservice.consumer.service.MigrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class ConsumerHandler {

    @Autowired private MigrationService migrationService;

    private final ExecutorService executorService;

    ConsumerHandler() {
        executorService = Executors.newFixedThreadPool(10);

    }

    /**
     * method is used to asynchronously migrate logs.
     * This is the entry method for the consumer
     * @return boolean value representing if the migration task
     * is submitted successfully or not.
     */
    public boolean handle() {
        try {
            Runnable migrateTaskRunnable = () -> migrationService.migrate();
            executorService.execute(migrateTaskRunnable);
            log.info("ConsumerHandler | handle | Successfully submitted task for migrating");
            return true;
        } catch (Exception e) {
            log.error("ConsumerHandler | handle | " +
                    "Exception occurred when trying to submit task for migrating {}", e.getMessage());
            return false;
        }
    }
}
