package com.jagdish.logprocessor.source;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class FileLogSource implements LogSource{

    private final BufferedReader reader;

    public FileLogSource(String filePath) throws FileNotFoundException {
        this.reader = new BufferedReader(new FileReader(filePath));
    }

    @Override
    public String read() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
