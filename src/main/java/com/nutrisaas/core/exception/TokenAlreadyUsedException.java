package com.nutrisaas.core.exception;

public class TokenAlreadyUsedException extends RuntimeException {
    public TokenAlreadyUsedException() { super("Token already used"); }
}