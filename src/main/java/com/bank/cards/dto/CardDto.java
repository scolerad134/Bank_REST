package com.bank.cards.dto;

import com.bank.cards.entity.Card;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.YearMonth;

public class CardDto {
    
    private Long id;
    
    @NotNull(message = "Card number is required")
    @Pattern(regexp = "\\d{16}", message = "Card number must be 16 digits")
    private String cardNumber;
    
    private String maskedCardNumber;
    
    @NotNull(message = "Card holder name is required")
    private String cardHolderName;
    
    @NotNull(message = "Expiry date is required")
    private YearMonth expiryDate;
    
    private BigDecimal balance;
    
    private Card.CardStatus status;
    
    @NotNull(message = "Card type is required")
    private Card.CardType cardType;
    
    private String ownerUsername;
    
    // Constructors
    public CardDto() {}
    
    public CardDto(Card card) {
        this.id = card.getId();
        this.cardNumber = card.getCardNumber();
        this.maskedCardNumber = card.getMaskedCardNumber();
        this.cardHolderName = card.getCardHolderName();
        this.expiryDate = card.getExpiryDate();
        this.balance = card.getBalance();
        this.status = card.getStatus();
        this.cardType = card.getCardType();
        this.ownerUsername = card.getOwner().getUsername();
    }
    
    // Getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getCardNumber() {
        return cardNumber;
    }
    
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }
    
    public String getMaskedCardNumber() {
        return maskedCardNumber;
    }
    
    public void setMaskedCardNumber(String maskedCardNumber) {
        this.maskedCardNumber = maskedCardNumber;
    }
    
    public String getCardHolderName() {
        return cardHolderName;
    }
    
    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }
    
    public YearMonth getExpiryDate() {
        return expiryDate;
    }
    
    public void setExpiryDate(YearMonth expiryDate) {
        this.expiryDate = expiryDate;
    }
    
    public BigDecimal getBalance() {
        return balance;
    }
    
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
    
    public Card.CardStatus getStatus() {
        return status;
    }
    
    public void setStatus(Card.CardStatus status) {
        this.status = status;
    }
    
    public Card.CardType getCardType() {
        return cardType;
    }
    
    public void setCardType(Card.CardType cardType) {
        this.cardType = cardType;
    }
    
    public String getOwnerUsername() {
        return ownerUsername;
    }
    
    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }
}