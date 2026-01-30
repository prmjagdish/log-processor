package com.jagdish.logprocessor.model;

import java.time.LocalDateTime;

public class LogEvent {

    private final LocalDateTime timestamp;
    private final LogLevel level;
    private final String message;
    private final String source;

    public LogEvent(LocalDateTime timestamp, LogLevel level, String message, String source) {
        this.timestamp = timestamp;
        this.level = level;
        this.message = message;
        this.source = source;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public LogLevel getLevel() {
        return level;
    }

    public String getMessage() {
        return message;
    }

    public String getSource() {
        return source;
    }

    @Override
    public String toString() {
        return "LogEvent{" +
                "timestamp=" + timestamp +
                ", level=" + level +
                ", message='" + message + '\'' +
                ", source='" + source + '\'' +
                '}';
    }
}
