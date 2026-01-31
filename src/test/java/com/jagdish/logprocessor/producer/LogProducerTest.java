package com.jagdish.logprocessor.producer;

import com.jagdish.logprocessor.model.LogEvent;
import com.jagdish.logprocessor.model.LogLevel;
import com.jagdish.logprocessor.queue.EventQueue;
import com.jagdish.logprocessor.source.LogSource;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

class LogProducerTest {

    @Test
    void shouldPublishEvents() throws Exception {

        LogSource source = mock(LogSource.class);
        EventQueue queue = mock(EventQueue.class);
        LogParser parser = mock(LogParser.class);
        LocalDateTime now = LocalDateTime.now();

        when(source.read())
                .thenReturn("log1")
                .thenReturn("log2")
                .thenReturn(null);

        LogEvent e1 = new LogEvent(now, LogLevel.ERROR,"Payment failed","PaymentService");
        LogEvent e2 = new LogEvent( now,LogLevel.ERROR,"Payment failed","PaymentService");

        when(parser.parse("log1")).thenReturn(e1);
        when(parser.parse("log2")).thenReturn(e2);

        LogProducer producer = new LogProducer(source, queue, parser);

        producer.run();

        verify(queue).publish(e1);
        verify(queue).publish(e2);
    }
}
