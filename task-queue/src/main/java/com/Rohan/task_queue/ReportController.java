package com.Rohan.task_queue;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private final MessageQueueService queueService;

    public ReportController(MessageQueueService queueService) {
        this.queueService = queueService;
    }

    @PostMapping
    public ResponseEntity<String> generateReport() throws InterruptedException {
        String taskName = "Report_Task_" + System.currentTimeMillis();
        queueService.addMessage(taskName);

        return ResponseEntity.accepted().body("Report generation started in the background for: " + taskName);
    }
}
