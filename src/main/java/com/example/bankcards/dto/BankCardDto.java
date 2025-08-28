package com.example.bankcards.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.example.bankcards.entity.CardStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankCardDto {
    private Long id;
    private String maskedNumber;
    private String cardholderName;
    private LocalDate expiryDate;
    private CardStatus status;
    private BigDecimal balance;
    private LocalDateTime createdAt;
}