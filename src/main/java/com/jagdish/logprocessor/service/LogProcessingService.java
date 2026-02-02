package com.jagdish.logprocessor.service;

import com.jagdish.logprocessor.aggregator.LogAggregator;
import com.jagdish.logprocessor.queue.EventQueue;
import com.jagdish.logprocessor.worker.LogProcessor;
import com.jagdish.logprocessor.worker.LogWorker;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class LogProcessingService {

    private final ExecutorService executor;
    private final EventQueue queue;
    private final int workerCount;
    private final LogProcessor processor;
    private final LogAggregator aggregator;

    public LogProcessingService(EventQueue queue,
                                ExecutorService executor,
                                int workerCount,
                                LogProcessor processor,
                                LogAggregator aggregator) {
        this.queue = queue;
        this.executor = executor;
        this.workerCount = workerCount;
        this.processor = processor;
        this.aggregator = aggregator;
    }

    public void start() {
        for (int i = 0; i < workerCount; i++) {
            executor.submit(new LogWorker(queue, processor, aggregator));
        }
    }

    public void stop() throws InterruptedException {
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
    }

    public void printMetrics() {
        aggregator.printMetrics();
    }
}

