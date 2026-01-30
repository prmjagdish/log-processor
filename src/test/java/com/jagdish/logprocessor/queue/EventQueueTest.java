package com.jagdish.logprocessor.queue;

import com.jagdish.logprocessor.model.LogEvent;
import com.jagdish.logprocessor.model.LogLevel;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

public class EventQueueTest {

    LocalDateTime now = LocalDateTime.now();

    @Test
    void shouldPublishAndConsumeSameEvent() throws Exception {

        EventQueue queue = new EventQueue();
        LogEvent event = new LogEvent(now, LogLevel.INFO, "msg", "service");

        queue.publish(event);
        LogEvent result = queue.consume();

        assertEquals(event, result);
    }

    @Test
    void shouldMaintainOrder() throws Exception {

        EventQueue queue = new EventQueue();

        LogEvent e1 = new LogEvent(now, LogLevel.DEBUG, "A", "S");
        LogEvent e2 = new LogEvent(now, LogLevel.WARN, "B", "S");

        queue.publish(e1);
        queue.publish(e2);

        assertEquals(e1, queue.consume());
        assertEquals(e2, queue.consume());
    }

    @Test
    void shouldBlockUntilEventArrives() throws Exception {

        EventQueue queue = new EventQueue();

        Thread producer = new Thread(() -> {
            try {
                Thread.sleep(200);
                queue.publish(new LogEvent(now, LogLevel.WARN, "B", "S"));;
            } catch (Exception ignored) {}
        });

        producer.start();
        long start = System.currentTimeMillis();

        queue.consume();
        long end = System.currentTimeMillis();

        assertTrue(end - start >= 200);
    }
}
