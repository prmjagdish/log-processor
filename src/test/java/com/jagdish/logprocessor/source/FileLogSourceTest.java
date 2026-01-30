package com.jagdish.logprocessor.source;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FileLogSourceTest {

    @Test
    void shouldReadAllLinesFromFile() throws Exception {

        LogSource source = new FileLogSource("src/main/java/com/jagdish/logprocessor/logs/app.log");

        int count = 0;
        String line;

        while ((line = source.read()) != null) {
            assertNotNull(line);
            System.out.println(line);
            count++;
        }

        assertTrue(count > 0);
    }
}
