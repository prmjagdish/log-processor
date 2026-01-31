package com.jagdish.logprocessor.producer;

import com.jagdish.logprocessor.model.LogEvent;
import com.jagdish.logprocessor.queue.EventQueue;
import com.jagdish.logprocessor.source.LogSource;

public class LogProducer implements Runnable {

    private final LogSource source;
    private final EventQueue queue;
    private final LogParser parser;

    public LogProducer(LogSource source, EventQueue queue, LogParser parser) {
        this.source = source;
        this.queue = queue;
        this.parser = parser;
    }

    @Override
    public void run() {

        String line;

        while ((line = source.read()) != null) {
            LogEvent event = parser.parse(line);

            try {
                queue.publish(event);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}

