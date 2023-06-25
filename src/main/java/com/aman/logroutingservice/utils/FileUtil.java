package com.aman.logroutingservice.utils;

import org.springframework.stereotype.Component;

import java.io.File;


@Component
public class FileUtil {

    public static double getFileSizeMegaBytes(File file) {
        return (double) file.length() / (1024 * 1024);
    }

    public static double getFileSizeKiloBytes(File file) {
        return (double) file.length() / 1024 ;
    }

    public static double getFileSizeBytes(File file) {
        return file.length();
    }
}
