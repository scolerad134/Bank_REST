package com.example.bankcards.dto;

import com.example.bankcards.entity.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDto {
    private Long id;
    private String fromCardMasked;
    private String toCardMasked;
    private BigDecimal amount;
    private TransactionStatus status;
    private String description;
    private LocalDateTime createdAt;
    private Long fromCardId;
    private Long toCardId;
}