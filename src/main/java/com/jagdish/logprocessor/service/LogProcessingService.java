package com.jagdish.logprocessor.service;

import com.jagdish.logprocessor.queue.EventQueue;
import com.jagdish.logprocessor.worker.LogWorker;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class LogProcessingService {

    private final ExecutorService executor;
    private final EventQueue queue;
    private final int workerCount;

    public LogProcessingService(EventQueue queue,
                                ExecutorService executor,
                                int workerCount) {
        this.queue = queue;
        this.executor = executor;
        this.workerCount = workerCount;
    }

    public void start() {
        for (int i = 0; i < workerCount; i++) {
            executor.submit(new LogWorker(queue));
        }
    }

    public void stop() throws InterruptedException {
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
    }
}

