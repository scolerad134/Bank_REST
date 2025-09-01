package com.example.bankcards.exception;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String message) {
        super(message);
    }

    public AccessDeniedException(String resource, Long id) {
        super("Access denied to " + resource + " with id: " + id);
    }
}
