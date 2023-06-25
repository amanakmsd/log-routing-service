package com.aman.logroutingservice.utils;


import java.util.concurrent.Semaphore;


public class MutexUtil {
    public static Semaphore mutex = new Semaphore(1);
}
