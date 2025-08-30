package com.example.bankcards.controller;

import com.example.bankcards.dto.CreateTransactionRequest;
import com.example.bankcards.dto.TransactionDto;
import com.example.bankcards.entity.Transaction;
import com.example.bankcards.entity.TransactionStatus;
import com.example.bankcards.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    
    private final TransactionService transactionService;
    
    @PostMapping("/transfer")
    public ResponseEntity<TransactionDto> transferMoney(
            @Valid @RequestBody CreateTransactionRequest request,
            @RequestParam Long userId) {
        
        TransactionDto transaction = transactionService.createTransaction(request, userId);
        return ResponseEntity.ok(transaction);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TransactionDto> getTransaction(@PathVariable Long id) {
        TransactionDto transaction = transactionService.getTransactionById(id);
        return ResponseEntity.ok(transaction);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<TransactionDto>> getUserTransactions(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<TransactionDto> transactions = transactionService.getUserTransactions(userId, pageable);
        
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping
    public ResponseEntity<Page<TransactionDto>> getAllTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<TransactionDto> transactions = transactionService.getAllTransactions(pageable);
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<TransactionDto>> getTransactionsByStatus(
            @PathVariable TransactionStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<TransactionDto> transactions = transactionService.getTransactionsByStatus(status, pageable);
        
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/date-range")
    public ResponseEntity<Page<TransactionDto>> getTransactionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<TransactionDto> transactions = transactionService.getTransactionsByDateRange(startDate, endDate, pageable);
        
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/user/{userId}/all")
    public ResponseEntity<List<TransactionDto>> getAllUserTransactions(@PathVariable Long userId) {
        List<Transaction> transactions = transactionService.getTransactionsByUserId(userId);
        List<TransactionDto> transactionDtos = transactions.stream()
            .map(transaction -> transactionService.mapToDto(transaction))
            .toList();
        return ResponseEntity.ok(transactionDtos);
    }
}
