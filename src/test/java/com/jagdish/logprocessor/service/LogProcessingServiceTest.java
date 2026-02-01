package com.jagdish.logprocessor.service;

import com.jagdish.logprocessor.queue.EventQueue;
import com.jagdish.logprocessor.worker.LogWorker;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class LogProcessingServiceTest {

    @Test
    void shouldSubmitWorkersToExecutor() {

        EventQueue queue = mock(EventQueue.class);
        ExecutorService executor = mock(ExecutorService.class);

        int workers = 4;

        LogProcessingService service =
                new LogProcessingService(queue, executor, workers);

        service.start();

        verify(executor, times(workers)).submit(any(LogWorker.class));
    }

    @Test
    void shouldShutdownExecutorOnStop() throws Exception {

        EventQueue queue = mock(EventQueue.class);
        ExecutorService executor = mock(ExecutorService.class);

        LogProcessingService service =
                new LogProcessingService(queue, executor, 2);

        service.stop();

        verify(executor).shutdown();
    }

    @Test
    void shouldAwaitTerminationOnStop() throws Exception {

        EventQueue queue = mock(EventQueue.class);
        ExecutorService executor = mock(ExecutorService.class);

        when(executor.awaitTermination(anyLong(), any()))
                .thenReturn(true);

        LogProcessingService service =
                new LogProcessingService(queue, executor, 2);

        service.stop();

        verify(executor).awaitTermination(5, TimeUnit.SECONDS);
    }

    @Test
    void shouldStartWithoutCrash() {

        EventQueue queue = mock(EventQueue.class);
        ExecutorService executor = Executors.newFixedThreadPool(2);

        LogProcessingService service =
                new LogProcessingService(queue, executor, 2);

        service.start();

        executor.shutdownNow();
    }

}
