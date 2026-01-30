package com.jagdish.logprocessor.queue;

import com.jagdish.logprocessor.model.LogEvent;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class EventQueue {

    private final BlockingQueue<LogEvent> queue = new LinkedBlockingQueue<>();

    public void publish(LogEvent event) throws InterruptedException {
        queue.put(event);
    }

    public LogEvent consume() throws InterruptedException {
        return queue.take();
    }

}
