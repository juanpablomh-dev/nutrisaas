package com.nutrisaas.core.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super("The email is already registered: " + email);
    }
}