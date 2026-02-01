package com.jagdish.logprocessor.worker;

import com.jagdish.logprocessor.model.LogEvent;
import com.jagdish.logprocessor.model.LogLevel;
import com.jagdish.logprocessor.queue.EventQueue;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

public class LogWorkerTest {

    LocalDateTime now = LocalDateTime.now();

    @Test
    void shouldProcessSingleEvent() throws Exception {

        EventQueue queue = mock(EventQueue.class);

        LogEvent event = new LogEvent(now, LogLevel.ERROR,"Payment failed","PaymentService");

        when(queue.consume())
                .thenReturn(event)
                .thenThrow(new InterruptedException()); // stop loop

        LogWorker worker = spy(new LogWorker(queue));

        worker.run();

        verify(worker, times(1)).process(event);
    }

    @Test
    void shouldProcessMultipleEvents() throws Exception {

        EventQueue queue = mock(EventQueue.class);

        LogEvent e1 = new LogEvent(now, LogLevel.ERROR,"Payment failed","PaymentService");
        LogEvent e2 = new LogEvent( now,LogLevel.ERROR,"Payment failed","PaymentService");

        when(queue.consume())
                .thenReturn(e1)
                .thenReturn(e2)
                .thenThrow(new InterruptedException());

        LogWorker worker = spy(new LogWorker(queue));

        worker.run();

        verify(worker).process(e1);
        verify(worker).process(e2);
    }

    @Test
    void shouldStopWhenInterrupted() throws Exception {

        EventQueue queue = mock(EventQueue.class);

        when(queue.consume())
                .thenThrow(new InterruptedException());

        LogWorker worker = spy(new LogWorker(queue));

        worker.run();

        verify(worker, never()).process(any());
    }

    @Test
    void shouldKeepConsumingUntilInterrupted() throws Exception {

        EventQueue queue = mock(EventQueue.class);

        when(queue.consume())
                .thenReturn(new LogEvent(now, LogLevel.ERROR,"Payment failed","PaymentService"))
                .thenReturn(new LogEvent( now,LogLevel.ERROR,"Payment failed","PaymentService"))
                .thenThrow(new InterruptedException());

        LogWorker worker = spy(new LogWorker(queue));

        worker.run();

        verify(queue, times(3)).consume();
    }




}
