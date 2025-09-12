package com.bank.cards.controller;

import com.bank.cards.dto.CardDto;
import com.bank.cards.entity.Card;
import com.bank.cards.entity.User;
import com.bank.cards.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cards")
@Tag(name = "Cards", description = "Card management endpoints")
public class CardController {
    
    private final CardService cardService;
    
    @Autowired
    public CardController(CardService cardService) {
        this.cardService = cardService;
    }
    
    @PostMapping
    @Operation(summary = "Create a new card", description = "Create a new card for the authenticated user")
    public ResponseEntity<CardDto> createCard(@Valid @RequestBody CardDto cardDto,
                                             @AuthenticationPrincipal User user) {
        CardDto createdCard = cardService.createCard(cardDto, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCard);
    }
    
    @GetMapping
    @Operation(summary = "Get user cards", description = "Get all cards for the authenticated user")
    public ResponseEntity<List<CardDto>> getUserCards(@AuthenticationPrincipal User user) {
        List<CardDto> cards = cardService.getUserCards(user);
        return ResponseEntity.ok(cards);
    }
    
    @GetMapping("/{cardId}")
    @Operation(summary = "Get card by ID", description = "Get a specific card by its ID")
    public ResponseEntity<CardDto> getCardById(@PathVariable Long cardId,
                                              @AuthenticationPrincipal User user) {
        CardDto card = cardService.getCardById(cardId, user);
        return ResponseEntity.ok(card);
    }
    
    @PutMapping("/{cardId}/status")
    @Operation(summary = "Update card status", description = "Update the status of a card (ACTIVE, BLOCKED, EXPIRED)")
    public ResponseEntity<CardDto> updateCardStatus(@PathVariable Long cardId,
                                                   @RequestParam Card.CardStatus status,
                                                   @AuthenticationPrincipal User user) {
        CardDto updatedCard = cardService.updateCardStatus(cardId, status, user);
        return ResponseEntity.ok(updatedCard);
    }
    
    @DeleteMapping("/{cardId}")
    @Operation(summary = "Delete card", description = "Delete a card")
    public ResponseEntity<Void> deleteCard(@PathVariable Long cardId,
                                          @AuthenticationPrincipal User user) {
        cardService.deleteCard(cardId, user);
        return ResponseEntity.noContent().build();
    }
}