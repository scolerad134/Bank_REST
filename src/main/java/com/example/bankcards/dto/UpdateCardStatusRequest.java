package com.example.bankcards.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.example.bankcards.entity.CardStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCardStatusRequest {
    @NotNull(message = "Card status is required")
    private CardStatus status;
}