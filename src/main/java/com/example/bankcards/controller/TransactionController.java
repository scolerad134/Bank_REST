package com.example.bankcards.controller;

import com.example.bankcards.dto.CreateTransactionRequest;
import com.example.bankcards.dto.TransactionDto;
import com.example.bankcards.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    
    private final TransactionService transactionService;
    
    @PostMapping("/transfer")
    public ResponseEntity<TransactionDto> transferMoney(@Valid @RequestBody CreateTransactionRequest request) {
        // В реальном проекте здесь должна быть проверка аутентификации
        Long userId = 1L; // Заглушка
        
        TransactionDto transaction = transactionService.createTransaction(request, userId);
        return ResponseEntity.ok(transaction);
    }
    
    @GetMapping("/history")
    public ResponseEntity<Page<TransactionDto>> getTransactionHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        // В реальном проекте здесь должна быть проверка аутентификации
        Long userId = 1L; // Заглушка
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<TransactionDto> transactions = transactionService.getUserTransactions(userId, pageable);
        
        return ResponseEntity.ok(transactions);
    }
}
