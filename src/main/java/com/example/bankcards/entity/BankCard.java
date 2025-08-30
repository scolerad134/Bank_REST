package com.example.bankcards.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bank_cards")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "card_number", nullable = false, unique = true, length = 255)
    private String cardNumber; // зашифрованный номер
    
    @Column(name = "masked_number", nullable = false, length = 20)
    private String maskedNumber; // маскированный для отображения
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    
    @Column(name = "cardholder_name", nullable = false, length = 100)
    private String cardholderName;
    
    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CardStatus status;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
