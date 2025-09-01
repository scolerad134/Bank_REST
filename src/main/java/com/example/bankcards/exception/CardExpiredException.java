package com.example.bankcards.exception;

import java.time.LocalDate;

public class CardExpiredException extends RuntimeException {
    private final LocalDate expiryDate;

    public CardExpiredException(String message) {
        super(message);
        this.expiryDate = null;
    }

    public CardExpiredException(LocalDate expiryDate) {
        super("Card has expired. Expiry date: " + expiryDate);
        this.expiryDate = expiryDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }
}
