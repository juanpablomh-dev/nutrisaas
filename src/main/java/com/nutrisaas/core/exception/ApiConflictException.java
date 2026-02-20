package com.nutrisaas.core.exception;

public class ApiConflictException extends RuntimeException {

    public ApiConflictException() {
        super();
    }

    public ApiConflictException(String message) {
        super(message);
    }

    public ApiConflictException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApiConflictException(Throwable cause) {
        super(cause);
    }
}