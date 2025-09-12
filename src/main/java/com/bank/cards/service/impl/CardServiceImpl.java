package com.bank.cards.service.impl;

import com.bank.cards.dto.CardDto;
import com.bank.cards.entity.Card;
import com.bank.cards.entity.User;
import com.bank.cards.repository.CardRepository;
import com.bank.cards.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Transactional
public class CardServiceImpl implements CardService {
    
    private final CardRepository cardRepository;
    private final Random random = new Random();
    
    @Autowired
    public CardServiceImpl(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }
    
    @Override
    public CardDto createCard(CardDto cardDto, User owner) {
        Card card = new Card();
        
        // Use provided card number if available and valid, otherwise generate
        String cardNumber = cardDto.getCardNumber();
        if (cardNumber != null && !cardNumber.isEmpty() && cardNumber.matches("\\d{16}")) {
            // Check if card number already exists
            if (cardRepository.existsByCardNumber(cardNumber)) {
                throw new RuntimeException("Card number already exists");
            }
            card.setCardNumber(cardNumber);
        } else {
            card.setCardNumber(generateCardNumber());
        }
        
        card.setCardHolderName(cardDto.getCardHolderName());
        card.setExpiryDate(cardDto.getExpiryDate() != null ? cardDto.getExpiryDate() : YearMonth.now().plusYears(3));
        card.setCvv(generateCvv());
        card.setCardType(cardDto.getCardType());
        card.setOwner(owner);
        card.setBalance(new BigDecimal("100.00")); // Initial balance of 100 rubles for testing
        
        Card savedCard = cardRepository.save(card);
        return new CardDto(savedCard);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CardDto> getUserCards(User user) {
        List<Card> cards = cardRepository.findByOwner(user);
        return cards.stream()
                .map(CardDto::new)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public CardDto getCardById(Long cardId, User user) {
        Card card = cardRepository.findByIdAndOwner(cardId, user)
                .orElseThrow(() -> new RuntimeException("Card not found"));
        return new CardDto(card);
    }
    
    @Override
    public CardDto updateCardStatus(Long cardId, Card.CardStatus status, User user) {
        Card card = cardRepository.findByIdAndOwner(cardId, user)
                .orElseThrow(() -> new RuntimeException("Card not found"));
        
        card.setStatus(status);
        Card updatedCard = cardRepository.save(card);
        return new CardDto(updatedCard);
    }
    
    @Override
    public void deleteCard(Long cardId, User user) {
        Card card = cardRepository.findByIdAndOwner(cardId, user)
                .orElseThrow(() -> new RuntimeException("Card not found"));
        
        cardRepository.delete(card);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CardDto> getAllCards(User user) {
        return getUserCards(user);
    }
    
    private String generateCardNumber() {
        StringBuilder cardNumber;
        do {
            cardNumber = new StringBuilder();
            for (int i = 0; i < 16; i++) {
                cardNumber.append(random.nextInt(10));
            }
        } while (cardRepository.existsByCardNumber(cardNumber.toString()));
        
        return cardNumber.toString();
    }
    
    private String generateCvv() {
        StringBuilder cvv = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            cvv.append(random.nextInt(10));
        }
        return cvv.toString();
    }
}