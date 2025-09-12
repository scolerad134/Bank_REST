package com.bank.cards.service;

import com.bank.cards.dto.CardDto;
import com.bank.cards.entity.Card;
import com.bank.cards.entity.User;
import com.bank.cards.repository.CardRepository;
import com.bank.cards.service.impl.CardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private CardServiceImpl cardService;

    private User testUser;
    private Card testCard;
    private CardDto testCardDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setFullName("Test User");
        testUser.setEmail("test@example.com");

        testCard = new Card();
        testCard.setId(1L);
        testCard.setCardNumber("1234567890123456");
        testCard.setCardHolderName("Test User");
        testCard.setExpiryDate(YearMonth.of(2027, 12));
        testCard.setCvv("123");
        testCard.setBalance(BigDecimal.valueOf(1000.00));
        testCard.setStatus(Card.CardStatus.ACTIVE);
        testCard.setCardType(Card.CardType.DEBIT);
        testCard.setOwner(testUser);

        testCardDto = new CardDto();
        testCardDto.setCardHolderName("Test User");
        testCardDto.setExpiryDate(YearMonth.of(2027, 12));
        testCardDto.setCardType(Card.CardType.DEBIT);
    }

    @Test
    void createCard_Success() {
        // Given
        when(cardRepository.existsByCardNumber(anyString())).thenReturn(false);
        when(cardRepository.save(any(Card.class))).thenReturn(testCard);

        // When
        CardDto result = cardService.createCard(testCardDto, testUser);

        // Then
        assertNotNull(result);
        assertEquals(testCard.getId(), result.getId());
        assertEquals(testCard.getCardHolderName(), result.getCardHolderName());
        assertEquals(testCard.getCardType(), result.getCardType());
        assertEquals(testCard.getOwner().getUsername(), result.getOwnerUsername());
        
        verify(cardRepository, times(1)).save(any(Card.class));
    }

    @Test
    void getUserCards_Success() {
        // Given
        List<Card> cards = Arrays.asList(testCard);
        when(cardRepository.findByOwner(testUser)).thenReturn(cards);

        // When
        List<CardDto> result = cardService.getUserCards(testUser);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testCard.getId(), result.get(0).getId());
        assertEquals(testCard.getCardHolderName(), result.get(0).getCardHolderName());
        
        verify(cardRepository, times(1)).findByOwner(testUser);
    }

    @Test
    void getCardById_Success() {
        // Given
        when(cardRepository.findByIdAndOwner(1L, testUser)).thenReturn(Optional.of(testCard));

        // When
        CardDto result = cardService.getCardById(1L, testUser);

        // Then
        assertNotNull(result);
        assertEquals(testCard.getId(), result.getId());
        assertEquals(testCard.getCardHolderName(), result.getCardHolderName());
        
        verify(cardRepository, times(1)).findByIdAndOwner(1L, testUser);
    }

    @Test
    void getCardById_NotFound() {
        // Given
        when(cardRepository.findByIdAndOwner(1L, testUser)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cardService.getCardById(1L, testUser);
        });
        
        assertEquals("Card not found", exception.getMessage());
        verify(cardRepository, times(1)).findByIdAndOwner(1L, testUser);
    }

    @Test
    void updateCardStatus_Success() {
        // Given
        when(cardRepository.findByIdAndOwner(1L, testUser)).thenReturn(Optional.of(testCard));
        when(cardRepository.save(any(Card.class))).thenReturn(testCard);

        // When
        CardDto result = cardService.updateCardStatus(1L, Card.CardStatus.BLOCKED, testUser);

        // Then
        assertNotNull(result);
        assertEquals(Card.CardStatus.BLOCKED, testCard.getStatus());
        
        verify(cardRepository, times(1)).findByIdAndOwner(1L, testUser);
        verify(cardRepository, times(1)).save(testCard);
    }

    @Test
    void deleteCard_Success() {
        // Given
        when(cardRepository.findByIdAndOwner(1L, testUser)).thenReturn(Optional.of(testCard));

        // When
        cardService.deleteCard(1L, testUser);

        // Then
        verify(cardRepository, times(1)).findByIdAndOwner(1L, testUser);
        verify(cardRepository, times(1)).delete(testCard);
    }

    @Test
    void deleteCard_NotFound() {
        // Given
        when(cardRepository.findByIdAndOwner(1L, testUser)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cardService.deleteCard(1L, testUser);
        });
        
        assertEquals("Card not found", exception.getMessage());
        verify(cardRepository, times(1)).findByIdAndOwner(1L, testUser);
        verify(cardRepository, never()).delete(any());
    }
}