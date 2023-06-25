package com.aman.logroutingservice.producer.controller;

import com.aman.logroutingservice.model.log.LogRequest;
import com.aman.logroutingservice.model.log.LogResponse;
import com.aman.logroutingservice.producer.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class LogController {

    @Autowired
    private LogService logService;

    @PostMapping(
            value = "/log",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<LogResponse> log(@RequestBody LogRequest logRequest) {
        LogResponse logResponse = logService.log(logRequest);
        return ResponseEntity
                .created(URI.create(String.format("%s", logResponse.isStatus())))
                .body(logResponse);
    }


}
