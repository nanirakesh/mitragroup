package com.mitra.exception;

public class AutoAssignmentException extends RuntimeException {

    public AutoAssignmentException(String message) {
        super(message);
    }

    public AutoAssignmentException(String message, Throwable cause) {
        super(message, cause);
    }
}