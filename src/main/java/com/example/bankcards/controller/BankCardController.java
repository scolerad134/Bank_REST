package com.example.bankcards.controller;

import com.example.bankcards.dto.BankCardDto;
import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.dto.UpdateCardStatusRequest;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.Role;
import com.example.bankcards.service.BankCardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class BankCardController {
    
    private final BankCardService bankCardService;
    
    @PostMapping
    public ResponseEntity<BankCardDto> createCard(@Valid @RequestBody CreateCardRequest request) {
        // В реальном проекте здесь должна быть проверка роли пользователя
        return ResponseEntity.ok(bankCardService.createCard(request));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<BankCardDto> getCard(@PathVariable Long id) {
        // В реальном проекте здесь должна быть проверка аутентификации
        return ResponseEntity.ok(bankCardService.getCardById(id, 1L, Role.USER));
    }
    
    @GetMapping
    public ResponseEntity<Page<BankCardDto>> getUserCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) CardStatus status,
            @RequestParam(required = false) String searchTerm) {
        
        // В реальном проекте здесь должна быть проверка аутентификации
        Long userId = 1L; // Заглушка
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        
        return ResponseEntity.ok(bankCardService.getUserCards(userId, status, searchTerm, pageable));
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<BankCardDto> updateCardStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCardStatusRequest request) {
        
        // В реальном проекте здесь должна быть проверка роли ADMIN
        return ResponseEntity.ok(bankCardService.updateCardStatus(id, request, 1L, Role.ADMIN));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        // В реальном проекте здесь должна быть проверка роли ADMIN
        bankCardService.deleteCard(id, 1L, Role.ADMIN);
        return ResponseEntity.noContent().build();
    }
}
