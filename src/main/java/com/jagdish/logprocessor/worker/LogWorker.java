package com.jagdish.logprocessor.worker;

import com.jagdish.logprocessor.aggregator.LogAggregator;
import com.jagdish.logprocessor.model.LogEvent;
import com.jagdish.logprocessor.queue.EventQueue;

public class LogWorker implements Runnable{

    private final EventQueue queue;
    private final LogProcessor processor;
    private final LogAggregator aggregator;

    public LogWorker(EventQueue queue, LogProcessor processor, LogAggregator aggregator) {
        this.queue = queue;
        this.processor = processor;
        this.aggregator = aggregator;
    }

    @Override
    public void run() {
        while(true){
            try{
                LogEvent event = queue.consume();
                process(event);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    protected void process(LogEvent event){
        LogProcessor.ProcessedLog processed = processor.process(event);
        aggregator.record(processed.getLevel());
    }
}
