package com.jagdish.logprocessor.aggregator;

import com.jagdish.logprocessor.model.LogLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class LogAggregatorTest {

    private LogAggregator aggregator;

    @BeforeEach
    void setUp() {
        aggregator = new LogAggregator();
    }

    @Test
    void shouldInitializeWithZeroCounts() {
        assertEquals(0, aggregator.getTotal());
        assertEquals(0, aggregator.getErrors());
        assertEquals(0, aggregator.getWarnings());
    }

    @Test
    void shouldRecordErrorLog() {
        aggregator.record(LogLevel.ERROR);

        assertEquals(1, aggregator.getTotal());
        assertEquals(1, aggregator.getErrors());
        assertEquals(0, aggregator.getWarnings());
    }

    @Test
    void shouldRecordWarningLog() {
        aggregator.record(LogLevel.WARN);

        assertEquals(1, aggregator.getTotal());
        assertEquals(0, aggregator.getErrors());
        assertEquals(1, aggregator.getWarnings());
    }

    @Test
    void shouldRecordInfoLog() {
        aggregator.record(LogLevel.INFO);

        assertEquals(1, aggregator.getTotal());
        assertEquals(0, aggregator.getErrors());
        assertEquals(0, aggregator.getWarnings());
    }

    @Test
    void shouldRecordDebugLog() {
        aggregator.record(LogLevel.DEBUG);

        assertEquals(1, aggregator.getTotal());
        assertEquals(0, aggregator.getErrors());
        assertEquals(0, aggregator.getWarnings());
    }

    @Test
    void shouldRecordMultipleLogs() {
        aggregator.record(LogLevel.ERROR);
        aggregator.record(LogLevel.ERROR);
        aggregator.record(LogLevel.WARN);
        aggregator.record(LogLevel.INFO);
        aggregator.record(LogLevel.DEBUG);

        assertEquals(5, aggregator.getTotal());
        assertEquals(2, aggregator.getErrors());
        assertEquals(1, aggregator.getWarnings());
    }

    @Test
    void shouldCalculateLogsPerSecond() throws InterruptedException {
        aggregator.record(LogLevel.INFO);
        aggregator.record(LogLevel.INFO);
        aggregator.record(LogLevel.INFO);

        Thread.sleep(100);

        double logsPerSecond = aggregator.getLogsPerSecond();
        assertTrue(logsPerSecond > 0, "Logs per second should be greater than 0");
        assertTrue(logsPerSecond < 100, "Logs per second should be reasonable");
    }

    @Test
    void shouldReturnZeroLogsPerSecondWhenNoTimeElapsed() {
        LogAggregator newAggregator = new LogAggregator();
        newAggregator.record(LogLevel.INFO);

        double logsPerSecond = newAggregator.getLogsPerSecond();
        assertTrue(logsPerSecond >= 0, "Logs per second should be non-negative");
    }

    @Test
    void shouldPrintMetricsCorrectly() {
        aggregator.record(LogLevel.ERROR);
        aggregator.record(LogLevel.ERROR);
        aggregator.record(LogLevel.WARN);
        aggregator.record(LogLevel.INFO);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            aggregator.printMetrics();
            String output = outputStream.toString();

            assertTrue(output.contains("Total: 4"));
            assertTrue(output.contains("Errors: 2"));
            assertTrue(output.contains("Warnings: 1"));
            assertTrue(output.contains("Logs/sec:"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void shouldBeThreadSafe() throws InterruptedException {
        int threadCount = 10;
        int recordsPerThread = 1000;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < recordsPerThread; j++) {
                        if (threadId % 3 == 0) {
                            aggregator.record(LogLevel.ERROR);
                        } else if (threadId % 3 == 1) {
                            aggregator.record(LogLevel.WARN);
                        } else {
                            aggregator.record(LogLevel.INFO);
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(5, TimeUnit.SECONDS);
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        long expectedTotal = threadCount * recordsPerThread;
        assertEquals(expectedTotal, aggregator.getTotal());

        long expectedErrors = 4 * recordsPerThread; 
        assertEquals(expectedErrors, aggregator.getErrors());

        long expectedWarnings = 3 * recordsPerThread; 
        assertEquals(expectedWarnings, aggregator.getWarnings());
    }

    @Test
    void shouldHandleLargeNumberOfRecords() {
        int recordCount = 50000;
        
        for (int i = 0; i < recordCount; i++) {
            if (i % 10 == 0) {
                aggregator.record(LogLevel.ERROR);
            } else if (i % 5 == 0) {
                aggregator.record(LogLevel.WARN);
            } else {
                aggregator.record(LogLevel.INFO);
            }
        }

        assertEquals(recordCount, aggregator.getTotal());
        assertEquals(5000, aggregator.getErrors()); 
        assertEquals(5000, aggregator.getWarnings()); 
    }

    @Test
    void shouldMaintainAccuracyWithMixedLogLevels() {
        for (int i = 0; i < 100; i++) {
            if (i % 4 == 0) {
                aggregator.record(LogLevel.ERROR);
            } else if (i % 4 == 1) {
                aggregator.record(LogLevel.WARN);
            } else if (i % 4 == 2) {
                aggregator.record(LogLevel.INFO);
            } else {
                aggregator.record(LogLevel.DEBUG);
            }
        }

        assertEquals(100, aggregator.getTotal());
        assertEquals(25, aggregator.getErrors()); 
        assertEquals(25, aggregator.getWarnings()); 
    }
}
