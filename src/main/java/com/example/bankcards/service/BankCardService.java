package com.example.bankcards.service;

import com.example.bankcards.dto.BankCardDto;
import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.dto.UpdateCardStatusRequest;
import com.example.bankcards.entity.BankCard;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.Role;
import com.example.bankcards.repository.BankCardRepository;
import com.example.bankcards.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;

@Service
@Transactional
@Slf4j
public class BankCardService {
    private final BankCardRepository bankCardRepository;
    private final UserRepository userRepository;
    
    public BankCardService(BankCardRepository bankCardRepository, UserRepository userRepository) {
        this.bankCardRepository = bankCardRepository;
        this.userRepository = userRepository;
    }
    
    public BankCardDto createCard(CreateCardRequest request) {
        User owner = userRepository.findById(request.getOwnerId())
            .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getOwnerId()));
        
        // Генерируем номер карты (16 цифр)
        String cardNumber = generateCardNumber();
        String maskedNumber = maskCardNumber(cardNumber);
        
        BankCard card = BankCard.builder()
            .cardNumber(cardNumber) // В реальном проекте здесь должно быть шифрование
            .maskedNumber(maskedNumber)
            .owner(owner)
            .cardholderName(request.getCardholderName())
            .expiryDate(LocalDate.now().plusYears(4)) // карта на 4 года
            .status(CardStatus.ACTIVE)
            .balance(BigDecimal.ZERO)
            .build();
        
        BankCard savedCard = bankCardRepository.save(card);
        log.info("Created new card: {} for user: {}", maskedNumber, owner.getUsername());
        
        return mapToDto(savedCard);
    }
    
    public BankCardDto getCardById(Long id, Long userId, Role userRole) {
        BankCard card = bankCardRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Card not found with id: " + id));
        
        // Проверяем права доступа
        if (userRole != Role.ADMIN && !card.getOwner().getId().equals(userId)) {
            throw new RuntimeException("Access denied to card: " + id);
        }
        
        return mapToDto(card);
    }
    
    public Page<BankCardDto> getUserCards(Long userId, CardStatus status, 
                                        String searchTerm, Pageable pageable) {
        Page<BankCard> cards = bankCardRepository.findByOwnerIdWithFilters(
            userId, status, searchTerm, pageable);
        
        return cards.map(this::mapToDto);
    }
    
    public BankCardDto updateCardStatus(Long id, UpdateCardStatusRequest request, 
                                      Long userId, Role userRole) {
        BankCard card = bankCardRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Card not found with id: " + id));
        
        // Только админ может менять статус карт
        if (userRole != Role.ADMIN) {
            throw new RuntimeException("Only admin can update card status");
        }
        
        card.setStatus(request.getStatus());
        BankCard savedCard = bankCardRepository.save(card);
        log.info("Updated card status: {} to {}", id, request.getStatus());
        
        return mapToDto(savedCard);
    }
    
    public void deleteCard(Long id, Long userId, Role userRole) {
        BankCard card = bankCardRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Card not found with id: " + id));
        
        // Только админ может удалять карты
        if (userRole != Role.ADMIN) {
            throw new RuntimeException("Only admin can delete cards");
        }
        
        bankCardRepository.deleteById(id);
        log.info("Deleted card: {}", id);
    }
    
    private String generateCardNumber() {
        // Простая генерация номера карты (в реальном проекте нужна более сложная логика)
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 16; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
    
    private String maskCardNumber(String cardNumber) {
        // Маскируем номер карты: **** **** **** 1234
        return "**** **** **** " + cardNumber.substring(12);
    }
    
    private BankCardDto mapToDto(BankCard card) {
        return BankCardDto.builder()
            .id(card.getId())
            .maskedNumber(card.getMaskedNumber())
            .cardholderName(card.getCardholderName())
            .expiryDate(card.getExpiryDate())
            .status(card.getStatus())
            .balance(card.getBalance())
            .createdAt(card.getCreatedAt())
            .build();
    }
}
