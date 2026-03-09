package com.Rohan.RateLimiter;

import java.util.concurrent.ConcurrentHashMap;

public class TimeTest {
    public static void main(String[] args) throws InterruptedException {
        ConcurrentHashMap<String, Long> TimeStamp = new ConcurrentHashMap<>();

        TimeStamp.computeIfAbsent("192.168.1.1", time -> System.currentTimeMillis());

        try {
            Thread.sleep(2000);
        }
        catch(InterruptedException e){

            System.out.println("The sleep was interupted");

        }

        Long curr_time = System.currentTimeMillis();
        Long prev_time = TimeStamp.get("192.168.1.1");

        long time_diff = curr_time - prev_time;

        if(time_diff > 5000){
            System.out.println("window expired, reset count!");
        }
        else{
            System.out.println("still in the window");
        }
    }
}
