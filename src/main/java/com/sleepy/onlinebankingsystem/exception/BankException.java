package com.sleepy.onlinebankingsystem.exception;

import lombok.Getter;

@Getter
public class BankException extends Exception {
    private final String userMessage;
    private final String logMessage;

    public BankException(String userMessage, String logMessage) {
        super(logMessage);
        this.userMessage = userMessage;
        this.logMessage = logMessage;
    }

    public BankException(String message) {
        this(message, message);
    }
}