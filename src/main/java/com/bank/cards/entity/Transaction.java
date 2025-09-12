package com.bank.cards.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_card_id")
    @JsonIgnoreProperties({"transactions", "owner"})
    private Card fromCard;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_card_id")
    @JsonIgnoreProperties({"transactions", "owner"})
    private Card toCard;
    
    @Column(nullable = false)
    private BigDecimal amount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status = TransactionStatus.PENDING;
    
    private String description;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // Constructors
    public Transaction() {}
    
    public Transaction(Card fromCard, Card toCard, BigDecimal amount, 
                      TransactionType type, String description) {
        this.fromCard = fromCard;
        this.toCard = toCard;
        this.amount = amount;
        this.type = type;
        this.description = description;
    }
    
    // Getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Card getFromCard() {
        return fromCard;
    }
    
    public void setFromCard(Card fromCard) {
        this.fromCard = fromCard;
    }
    
    public Card getToCard() {
        return toCard;
    }
    
    public void setToCard(Card toCard) {
        this.toCard = toCard;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public TransactionType getType() {
        return type;
    }
    
    public void setType(TransactionType type) {
        this.type = type;
    }
    
    public TransactionStatus getStatus() {
        return status;
    }
    
    public void setStatus(TransactionStatus status) {
        this.status = status;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public enum TransactionType {
        TRANSFER, DEPOSIT, WITHDRAWAL
    }
    
    public enum TransactionStatus {
        PENDING, COMPLETED, FAILED, CANCELLED
    }
}