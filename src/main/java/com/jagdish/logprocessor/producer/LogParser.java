package com.jagdish.logprocessor.producer;

import com.jagdish.logprocessor.model.LogEvent;

public interface LogParser {
    LogEvent parse(String line);
}
