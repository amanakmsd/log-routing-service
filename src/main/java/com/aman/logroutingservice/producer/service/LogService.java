package com.aman.logroutingservice.producer.service;

import com.aman.logroutingservice.config.FileConfig;
import com.aman.logroutingservice.consumer.handler.ConsumerHandler;
import com.aman.logroutingservice.model.log.LogRequest;
import com.aman.logroutingservice.model.log.LogResponse;
import com.aman.logroutingservice.repository.file.LogFileRepository;
import com.aman.logroutingservice.utils.MutexUtil;
import com.aman.logroutingservice.utils.QueueUtil;
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
        return new LogResponse(QueueUtil.insert(logRequest));
    }


}
