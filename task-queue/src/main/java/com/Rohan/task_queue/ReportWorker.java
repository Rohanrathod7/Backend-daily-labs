package com.Rohan.task_queue;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class ReportWorker {

    private final MessageQueueService queueService;

    public ReportWorker(MessageQueueService queueService) {
        this.queueService = queueService;
    }
    @PostConstruct
    public void startWorker() throws InterruptedException {
        new Thread(()-> {
            while (true){
                try {
                    String task = queueService.getMessage();
                    System.out.println("Processing: " + task);
                    Thread.sleep(5000);
                    System.out.println("Finished: " + task);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();


    }
}
