package com.bank.cards.controller;

import com.bank.cards.dto.TransferRequest;
import com.bank.cards.entity.Transaction;
import com.bank.cards.entity.User;
import com.bank.cards.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
@Tag(name = "Transactions", description = "Transaction management endpoints")
public class TransactionController {
    
    private final TransactionService transactionService;
    
    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }
    
    @PostMapping("/transfer")
    @Operation(summary = "Transfer between own cards", description = "Transfer money between user's own cards")
    public ResponseEntity<Transaction> transferBetweenOwnCards(@Valid @RequestBody TransferRequest transferRequest,
                                                              @AuthenticationPrincipal User user) {
        Transaction transaction = transactionService.transferBetweenOwnCards(transferRequest, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
    }
    
    @GetMapping
    @Operation(summary = "Get user transactions", description = "Get all transactions for the authenticated user")
    public ResponseEntity<Page<Transaction>> getUserTransactions(@RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "20") int size,
                                                                @AuthenticationPrincipal User user) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> transactions = transactionService.getUserTransactions(user, pageable);
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/card/{cardId}")
    @Operation(summary = "Get card transactions", description = "Get all transactions for a specific card")
    public ResponseEntity<Page<Transaction>> getCardTransactions(@PathVariable Long cardId,
                                                                @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "20") int size,
                                                                @AuthenticationPrincipal User user) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> transactions = transactionService.getCardTransactions(cardId, user, pageable);
        return ResponseEntity.ok(transactions);
    }
}