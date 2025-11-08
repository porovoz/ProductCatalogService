package com.bestapp.com.audit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Simple audit logger that prints timestamped events to the console.
 * Used for tracking user actions and important system events.
 */
public class AuditLogger {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    /**
     * Logs a message with the current timestamp to the console.
     *
     * @param action description of the user or system action (e.g. "admin added product").
     */
    public void log(String action) {
        System.out.println("[" + LocalDateTime.now().format(formatter) + "] Action: " + action);
    }

}
