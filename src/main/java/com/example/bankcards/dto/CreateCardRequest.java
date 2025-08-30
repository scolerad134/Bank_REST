package com.example.bankcards.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCardRequest {
    @NotBlank(message = "Cardholder name is required")
    @Size(max = 100, message = "Cardholder name must not exceed 100 characters")
    private String cardholderName;
    
    @NotNull(message = "Owner ID is required")
    @Positive(message = "Owner ID must be positive")
    private Long ownerId;
    
    @NotNull(message = "Initial balance is required")
    @Positive(message = "Initial balance must be positive")
    private BigDecimal initialBalance;
}
