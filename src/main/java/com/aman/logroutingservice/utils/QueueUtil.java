package com.aman.logroutingservice.utils;

import com.aman.logroutingservice.model.log.LogRequest;

import java.util.concurrent.ConcurrentLinkedQueue;

public class QueueUtil {

    private static ConcurrentLinkedQueue<LogRequest> LOG_REQUEST_QUEUE = new ConcurrentLinkedQueue<>();

    public static boolean insert(LogRequest logRequest) {
        return LOG_REQUEST_QUEUE.add(logRequest);
    }

    public static LogRequest poll() {
        return LOG_REQUEST_QUEUE.poll();
    }

    public static boolean isEmpty() {
        return LOG_REQUEST_QUEUE.isEmpty();
    }

    public static int size() {
        return LOG_REQUEST_QUEUE.size();
    }

    public static LogRequest peek() {
        return LOG_REQUEST_QUEUE.peek();
    }
}
