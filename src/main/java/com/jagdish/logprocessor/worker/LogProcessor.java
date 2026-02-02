package com.jagdish.logprocessor.worker;

import com.jagdish.logprocessor.model.LogEvent;
import com.jagdish.logprocessor.model.LogLevel;

import java.time.LocalDateTime;

public class LogProcessor {

    public ProcessedLog process(LogEvent event) {
        LogLevel level = event.getLevel();
        LocalDateTime timestamp = event.getTimestamp();
        
        return new ProcessedLog(level, timestamp);
    }

    public static class ProcessedLog {
        private final LogLevel level;
        private final LocalDateTime timestamp;

        public ProcessedLog(LogLevel level, LocalDateTime timestamp) {
            this.level = level;
            this.timestamp = timestamp;
        }

        public LogLevel getLevel() {
            return level;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }
    }
}
