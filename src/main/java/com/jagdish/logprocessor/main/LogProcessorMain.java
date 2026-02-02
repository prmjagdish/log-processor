package com.jagdish.logprocessor.main;

import com.jagdish.logprocessor.aggregator.LogAggregator;
import com.jagdish.logprocessor.producer.LogParse;
import com.jagdish.logprocessor.producer.LogParser;
import com.jagdish.logprocessor.producer.LogProducer;
import com.jagdish.logprocessor.queue.EventQueue;
import com.jagdish.logprocessor.service.LogProcessingService;
import com.jagdish.logprocessor.source.FileLogSource;
import com.jagdish.logprocessor.worker.LogProcessor;

import java.io.FileNotFoundException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LogProcessorMain {

    private static final String LOG_FILE_PATH = "src/main/java/com/jagdish/logprocessor/logs/app.log";
    private static final int WORKER_COUNT = 4; 

    public static void main(String[] args) {
        System.out.println("=== Log Processor - Concurrent Processing Demo ===");
        System.out.println("Starting log processing with " + WORKER_COUNT + " concurrent workers...\n");

        try {
            EventQueue queue = new EventQueue();
            LogAggregator aggregator = new LogAggregator();
            LogProcessor processor = new LogProcessor();
            LogParser parser = new LogParse();

            ExecutorService producerExecutor = Executors.newSingleThreadExecutor();
            ExecutorService workerExecutor = Executors.newFixedThreadPool(WORKER_COUNT);

            FileLogSource logSource = new FileLogSource(LOG_FILE_PATH);
            LogProducer producer = new LogProducer(logSource, queue, parser);
            producerExecutor.submit(producer);

            LogProcessingService processingService = new LogProcessingService(
                    queue, workerExecutor, WORKER_COUNT, processor, aggregator);
            processingService.start();

            System.out.println("✓ Producer started - reading logs from: " + LOG_FILE_PATH);
            System.out.println("✓ " + WORKER_COUNT + " worker threads started - processing logs concurrently");
            System.out.println("  (Workers will process logs from the shared queue in parallel)\n");

            producerExecutor.shutdown();
            boolean producerFinished = producerExecutor.awaitTermination(30, TimeUnit.SECONDS);
            
            if (!producerFinished) {
                System.out.println("Warning: Producer did not finish within timeout");
            } else {
                System.out.println("✓ Producer finished reading all logs from file");
            }

            System.out.println("Waiting for workers to finish processing remaining logs...");
            Thread.sleep(2000);

            processingService.stop();
            System.out.println("✓ All workers stopped\n");

            System.out.println("=== Processing Complete ===");
            System.out.println("Concurrent workers used: " + WORKER_COUNT);
            System.out.println("Final Metrics:");
            processingService.printMetrics();

        } catch (FileNotFoundException e) {
            System.err.println("Error: Log file not found at: " + LOG_FILE_PATH);
            System.err.println("Please ensure the log file exists.");
        } catch (InterruptedException e) {
            System.err.println("Error: Processing was interrupted");
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
