package com.jagdish.logprocessor.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LogEventTest {

    @Test
    void shouldCreateLogEventCorrectly() {
        LocalDateTime now = LocalDateTime.now();

        LogEvent event = new LogEvent(
                now,
                LogLevel.ERROR,
                "Payment failed",
                "PaymentService"
        );

        assertEquals(now, event.getTimestamp());
        assertEquals(LogLevel.ERROR, event.getLevel());
        assertEquals("Payment failed", event.getMessage());
        assertEquals("PaymentService", event.getSource());
    }
}
