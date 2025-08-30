package com.example.bankcards.controller;

import com.example.bankcards.dto.BankCardDto;
import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.dto.UpdateCardStatusRequest;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.service.BankCardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import com.example.bankcards.entity.BankCard;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class BankCardController {
    
    private final BankCardService bankCardService;
    
    @PostMapping
    public ResponseEntity<BankCardDto> createCard(@Valid @RequestBody CreateCardRequest request) {
        BankCardDto card = bankCardService.createCard(request);
        return ResponseEntity.ok(card);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<BankCardDto> getCard(@PathVariable Long id) {
        BankCardDto card = bankCardService.getCardById(id);
        return ResponseEntity.ok(card);
    }
    
    @GetMapping
    public ResponseEntity<Page<BankCardDto>> getAllCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<BankCardDto> cards = bankCardService.getAllCards(pageable);
        return ResponseEntity.ok(cards);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<BankCardDto>> getUserCards(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) CardStatus status,
            @RequestParam(required = false) String searchTerm) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<BankCardDto> cards = bankCardService.getUserCards(userId, status, searchTerm, pageable);
        
        return ResponseEntity.ok(cards);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<BankCardDto>> getCardsByStatus(
            @PathVariable CardStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<BankCardDto> cards = bankCardService.getCardsByStatus(status, pageable);
        
        return ResponseEntity.ok(cards);
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<BankCardDto> updateCardStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCardStatusRequest request) {
        
        BankCardDto card = bankCardService.updateCardStatus(id, request);
        return ResponseEntity.ok(card);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        bankCardService.deleteCard(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/expired")
    public ResponseEntity<List<BankCardDto>> getExpiredCards() {
        List<BankCard> expiredCards = bankCardService.getExpiredCards();
        List<BankCardDto> expiredCardDtos = expiredCards.stream()
            .map(card -> bankCardService.mapToDto(card))
            .toList();
        return ResponseEntity.ok(expiredCardDtos);
    }
    
    @GetMapping("/low-balance")
    public ResponseEntity<List<BankCardDto>> getLowBalanceCards(
            @RequestParam(defaultValue = "100") BigDecimal minBalance) {
        
        List<BankCard> lowBalanceCards = bankCardService.getLowBalanceCards(minBalance);
        List<BankCardDto> lowBalanceCardDtos = lowBalanceCards.stream()
            .map(card -> bankCardService.mapToDto(card))
            .toList();
        return ResponseEntity.ok(lowBalanceCardDtos);
    }
}
