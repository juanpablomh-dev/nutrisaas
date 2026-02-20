package com.nutrisaas.core.exception;

public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException() { super("Expired token"); }
}