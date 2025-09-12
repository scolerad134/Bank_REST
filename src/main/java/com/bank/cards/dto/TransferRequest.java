package com.bank.cards.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class TransferRequest {
    
    @NotNull(message = "From card ID is required")
    private Long fromCardId;
    
    @NotNull(message = "To card ID is required")
    private Long toCardId;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
    
    private String description;
    
    // Constructors
    public TransferRequest() {}
    
    public TransferRequest(Long fromCardId, Long toCardId, BigDecimal amount, String description) {
        this.fromCardId = fromCardId;
        this.toCardId = toCardId;
        this.amount = amount;
        this.description = description;
    }
    
    // Getters and setters
    public Long getFromCardId() {
        return fromCardId;
    }
    
    public void setFromCardId(Long fromCardId) {
        this.fromCardId = fromCardId;
    }
    
    public Long getToCardId() {
        return toCardId;
    }
    
    public void setToCardId(Long toCardId) {
        this.toCardId = toCardId;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}