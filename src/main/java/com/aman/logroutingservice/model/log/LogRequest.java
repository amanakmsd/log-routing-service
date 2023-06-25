package com.aman.logroutingservice.model.log;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
public class LogRequest {
    private String eventName;
    private Long timeStamp;
    private String source;
    private String sourceId;
    private String logMessage;
}
