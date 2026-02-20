package com.nutrisaas.core.exception;

public class TokenNotFoundException extends RuntimeException {
    public TokenNotFoundException() { super("Invalid or not found token"); }
}