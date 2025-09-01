package com.example.bankcards.service;

import com.example.bankcards.dto.BankCardDto;
import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.dto.UpdateCardStatusRequest;
import com.example.bankcards.entity.BankCard;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.Role;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.BankCardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.EncryptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Service
@Transactional
@Slf4j
public class BankCardService {
    private final BankCardRepository bankCardRepository;
    private final UserRepository userRepository;
    private final EncryptionService encryptionService;
    
    public BankCardService(BankCardRepository bankCardRepository, 
                          UserRepository userRepository,
                          EncryptionService encryptionService) {
        this.bankCardRepository = bankCardRepository;
        this.userRepository = userRepository;
        this.encryptionService = encryptionService;
    }
    
    public BankCardDto createCard(CreateCardRequest request) {
        User owner = userRepository.findById(request.getOwnerId())
            .orElseThrow(() -> new UserNotFoundException(request.getOwnerId()));
        
        String cardNumber = generateCardNumber();
        String encryptedCardNumber = encryptionService.encrypt(cardNumber);
        String maskedNumber = encryptionService.maskCardNumber(cardNumber);
        
        BankCard card = BankCard.builder()
            .cardNumber(encryptedCardNumber)
            .maskedNumber(maskedNumber)
            .owner(owner)
            .cardholderName(request.getCardholderName())
            .expiryDate(LocalDate.now().plusYears(4))
            .status(CardStatus.ACTIVE)
            .balance(request.getInitialBalance())
            .build();
        
        BankCard savedCard = bankCardRepository.save(card);
        log.info("Created new card: {} for user: {}", maskedNumber, owner.getUsername());
        
        return mapToDto(savedCard);
    }
    
    public BankCardDto getCardById(Long id) {
        BankCard card = bankCardRepository.findById(id)
            .orElseThrow(() -> new CardNotFoundException(id));
        return mapToDto(card);
    }
    
    public Page<BankCardDto> getUserCards(Long userId, CardStatus status, 
                                        String searchTerm, Pageable pageable) {
        Page<BankCard> cards = bankCardRepository.findByOwnerIdWithFilters(
            userId, status, searchTerm, pageable);
        
        return cards.map(this::mapToDto);
    }
    
    public Page<BankCardDto> getAllCards(Pageable pageable) {
        Page<BankCard> cards = bankCardRepository.findAll(pageable);
        return cards.map(this::mapToDto);
    }
    
    public Page<BankCardDto> getCardsByStatus(CardStatus status, Pageable pageable) {
        Page<BankCard> cards = bankCardRepository.findByStatus(status, pageable);
        return cards.map(this::mapToDto);
    }
    
    public BankCardDto updateCardStatus(Long id, UpdateCardStatusRequest request) {
        BankCard card = bankCardRepository.findById(id)
            .orElseThrow(() -> new CardNotFoundException(id));
        
        card.setStatus(request.getStatus());
        BankCard savedCard = bankCardRepository.save(card);
        log.info("Updated card status: {} to {}", id, request.getStatus());
        
        return mapToDto(savedCard);
    }
    
    public void deleteCard(Long id) {
        if (!bankCardRepository.existsById(id)) {
            throw new CardNotFoundException(id);
        }
        bankCardRepository.deleteById(id);
        log.info("Deleted card: {}", id);
    }
    
    public BankCardDto getCardBalance(Long id) {
        BankCard card = bankCardRepository.findById(id)
            .orElseThrow(() -> new CardNotFoundException(id));
        
        return BankCardDto.builder()
            .id(card.getId())
            .maskedNumber(card.getMaskedNumber())
            .balance(card.getBalance())
            .status(card.getStatus())
            .build();
    }
    
    private BankCardDto mapToDto(BankCard card) {
        return BankCardDto.builder()
            .id(card.getId())
            .maskedNumber(card.getMaskedNumber())
            .ownerId(card.getOwner().getId())
            .cardholderName(card.getCardholderName())
            .expiryDate(card.getExpiryDate())
            .status(card.getStatus())
            .balance(card.getBalance())
            .createdAt(card.getCreatedAt())
            .updatedAt(card.getUpdatedAt())
            .build();
    }
    
    private String generateCardNumber() {
        Random random = new Random();
        StringBuilder cardNumber = new StringBuilder();
        
        for (int i = 0; i < 16; i++) {
            cardNumber.append(random.nextInt(10));
        }
        
        return cardNumber.toString();
    }
    
    private String maskCardNumber(String cardNumber) {
        return encryptionService.maskCardNumber(cardNumber);
    }
}
