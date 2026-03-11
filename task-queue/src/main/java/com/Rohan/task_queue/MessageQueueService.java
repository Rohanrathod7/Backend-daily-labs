package com.Rohan.task_queue;

import org.springframework.stereotype.Service;

import java.util.concurrent.LinkedBlockingQueue;

@Service
public class MessageQueueService {

    private final LinkedBlockingQueue<String> taskQueue = new LinkedBlockingQueue<>();

    public void addMessage(String message) throws InterruptedException {
        taskQueue.put(message);
    }

    public String getMessage() throws InterruptedException {
        return taskQueue.take();
    }

 }
