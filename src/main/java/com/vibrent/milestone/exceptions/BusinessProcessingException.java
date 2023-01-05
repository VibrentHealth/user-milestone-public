package com.vibrent.milestone.exceptions;

/**
 * BusinessProcessingException
 */

public class BusinessProcessingException extends RuntimeException {

    private static final long serialVersionUID = 4107655493136110214L;

    public BusinessProcessingException(String message) {
        super(message);
    }

    public BusinessProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}