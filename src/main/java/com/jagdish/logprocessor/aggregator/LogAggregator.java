package com.jagdish.logprocessor.aggregator;

import com.jagdish.logprocessor.model.LogLevel;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

public class LogAggregator {

    private final ConcurrentHashMap<String, LongAdder> metrics = new ConcurrentHashMap<>();
    private final long startTime = System.currentTimeMillis();

    public LogAggregator() {
        metrics.put("total", new LongAdder());
        metrics.put("errors", new LongAdder());
        metrics.put("warnings", new LongAdder());
    }

    public void record(LogLevel level) {
        metrics.get("total").increment();
        
        if (level == LogLevel.ERROR) {
            metrics.get("errors").increment();
        } else if (level == LogLevel.WARN) {
            metrics.get("warnings").increment();
        }
    }

    public long getTotal() {
        return metrics.get("total").sum();
    }

    public long getErrors() {
        return metrics.get("errors").sum();
    }

    public long getWarnings() {
        return metrics.get("warnings").sum();
    }

    public double getLogsPerSecond() {
        long elapsedMillis = System.currentTimeMillis() - startTime;
        if (elapsedMillis == 0) {
            return 0.0;
        }
        return (metrics.get("total").sum() * 1000.0) / elapsedMillis;
    }

    public void printMetrics() {
        System.out.println("Total: " + getTotal());
        System.out.println("Errors: " + getErrors());
        System.out.println("Warnings: " + getWarnings());
        System.out.println("Logs/sec: " + (long) getLogsPerSecond());
    }
}
