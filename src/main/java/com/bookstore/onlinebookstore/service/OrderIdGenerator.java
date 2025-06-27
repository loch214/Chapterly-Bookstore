package com.bookstore.onlinebookstore.service;

import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class OrderIdGenerator {

    private static final String STATE_FILE_PATH = "src/main/resources/order_id_state.txt";
    private String currentPrefix;
    private int currentNumber;

    @PostConstruct
    public void initialize() {
        loadState();
    }

    private void loadState() {
        try {
            File stateFile = new File(STATE_FILE_PATH);
            if (!stateFile.exists()) {
                currentPrefix = "AAA";
                currentNumber = 0;
                saveState();
                return;
            }

            String content = new String(Files.readAllBytes(Paths.get(STATE_FILE_PATH)));
            String[] parts = content.split(",");
            if (parts.length == 2) {
                currentPrefix = parts[0];
                currentNumber = Integer.parseInt(parts[1]);
            } else {
                currentPrefix = "AAA";
                currentNumber = 0;
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            currentPrefix = "AAA";
            currentNumber = 0;
        }
    }

    private void saveState() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(STATE_FILE_PATH, false))) {
            writer.print(currentPrefix + "," + currentNumber);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized String generateNextId() {
        currentNumber++;
        if (currentNumber > 9999) {
            currentNumber = 1;
            currentPrefix = incrementPrefix(currentPrefix);
        }
        saveState();
        return String.format("%s%04d", currentPrefix, currentNumber);
    }

    private String incrementPrefix(String prefix) {
        char[] chars = prefix.toUpperCase().toCharArray();
        for (int i = chars.length - 1; i >= 0; i--) {
            if (chars[i] < 'Z') {
                chars[i]++;
                return new String(chars);
            } else {
                chars[i] = 'A';
            }
        }
        return "AAA"; // Rollover from ZZZ
    }
} 