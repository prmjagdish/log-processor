package com.jagdish.logprocessor.producer;

import com.jagdish.logprocessor.model.LogEvent;
import com.jagdish.logprocessor.model.LogLevel;

import java.time.LocalDateTime;

public class LogParse implements LogParser {
    @Override
    public LogEvent parse(String line) {
        String[] parts = line.split(" ", 4);

        LocalDateTime timestamp = LocalDateTime.parse(parts[0]);
        LogLevel level = LogLevel.valueOf(parts[1]);
        String source = parts[2];
        String message = parts[3];

        return new LogEvent(timestamp, level, message, source);
    }
}
