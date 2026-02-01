package com.jagdish.logprocessor.worker;

import com.jagdish.logprocessor.model.LogEvent;
import com.jagdish.logprocessor.queue.EventQueue;

public class LogWorker implements Runnable{

    private final EventQueue queue;

    public LogWorker(EventQueue queue) {
        this.queue = queue;
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
        System.out.println(event);
    }
}
