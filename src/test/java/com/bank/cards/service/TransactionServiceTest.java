package com.bank.cards.service;

import com.bank.cards.dto.TransferRequest;
import com.bank.cards.entity.Card;
import com.bank.cards.entity.Transaction;
import com.bank.cards.entity.User;
import com.bank.cards.repository.CardRepository;
import com.bank.cards.repository.TransactionRepository;
import com.bank.cards.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private User testUser;
    private Card fromCard;
    private Card toCard;
    private TransferRequest transferRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        fromCard = new Card();
        fromCard.setId(1L);
        fromCard.setCardNumber("1234567890123456");
        fromCard.setCardHolderName("Test User");
        fromCard.setBalance(BigDecimal.valueOf(1000.00));
        fromCard.setStatus(Card.CardStatus.ACTIVE);
        fromCard.setCardType(Card.CardType.DEBIT);
        fromCard.setOwner(testUser);

        toCard = new Card();
        toCard.setId(2L);
        toCard.setCardNumber("9876543210987654");
        toCard.setCardHolderName("Test User");
        toCard.setBalance(BigDecimal.valueOf(500.00));
        toCard.setStatus(Card.CardStatus.ACTIVE);
        toCard.setCardType(Card.CardType.CREDIT);
        toCard.setOwner(testUser);

        transferRequest = new TransferRequest();
        transferRequest.setFromCardId(1L);
        transferRequest.setToCardId(2L);
        transferRequest.setAmount(BigDecimal.valueOf(100.00));
        transferRequest.setDescription("Test transfer");
    }

    @Test
    void transferBetweenOwnCards_Success() {
        // Given
        when(cardRepository.findByIdAndOwner(1L, testUser)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findByIdAndOwner(2L, testUser)).thenReturn(Optional.of(toCard));
        when(cardRepository.save(fromCard)).thenReturn(fromCard);
        when(cardRepository.save(toCard)).thenReturn(toCard);
        
        Transaction savedTransaction = new Transaction();
        savedTransaction.setId(1L);
        savedTransaction.setFromCard(fromCard);
        savedTransaction.setToCard(toCard);
        savedTransaction.setAmount(BigDecimal.valueOf(100.00));
        savedTransaction.setType(Transaction.TransactionType.TRANSFER);
        savedTransaction.setStatus(Transaction.TransactionStatus.COMPLETED);
        
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

        // When
        Transaction result = transactionService.transferBetweenOwnCards(transferRequest, testUser);

        // Then
        assertNotNull(result);
        assertEquals(Transaction.TransactionStatus.COMPLETED, result.getStatus());
        assertEquals(BigDecimal.valueOf(100.00), result.getAmount());
        assertEquals(Transaction.TransactionType.TRANSFER, result.getType());
        
        // Verify balance changes
        assertEquals(BigDecimal.valueOf(900.00), fromCard.getBalance());
        assertEquals(BigDecimal.valueOf(600.00), toCard.getBalance());
        
        verify(cardRepository, times(1)).save(fromCard);
        verify(cardRepository, times(1)).save(toCard);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void transferBetweenOwnCards_InsufficientBalance() {
        // Given
        transferRequest.setAmount(BigDecimal.valueOf(1500.00)); // More than available balance
        when(cardRepository.findByIdAndOwner(1L, testUser)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findByIdAndOwner(2L, testUser)).thenReturn(Optional.of(toCard));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transactionService.transferBetweenOwnCards(transferRequest, testUser);
        });
        
        assertEquals("Insufficient balance", exception.getMessage());
        
        // Verify no changes were made
        assertEquals(BigDecimal.valueOf(1000.00), fromCard.getBalance());
        assertEquals(BigDecimal.valueOf(500.00), toCard.getBalance());
        
        verify(cardRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void transferBetweenOwnCards_FromCardNotFound() {
        // Given
        when(cardRepository.findByIdAndOwner(1L, testUser)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transactionService.transferBetweenOwnCards(transferRequest, testUser);
        });
        
        assertEquals("From card not found", exception.getMessage());
        
        verify(cardRepository, times(1)).findByIdAndOwner(1L, testUser);
        verify(cardRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void transferBetweenOwnCards_ToCardNotFound() {
        // Given
        when(cardRepository.findByIdAndOwner(1L, testUser)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findByIdAndOwner(2L, testUser)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transactionService.transferBetweenOwnCards(transferRequest, testUser);
        });
        
        assertEquals("To card not found", exception.getMessage());
        
        verify(cardRepository, times(1)).findByIdAndOwner(1L, testUser);
        verify(cardRepository, times(1)).findByIdAndOwner(2L, testUser);
        verify(cardRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void transferBetweenOwnCards_FromCardNotActive() {
        // Given
        fromCard.setStatus(Card.CardStatus.BLOCKED);
        when(cardRepository.findByIdAndOwner(1L, testUser)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findByIdAndOwner(2L, testUser)).thenReturn(Optional.of(toCard));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transactionService.transferBetweenOwnCards(transferRequest, testUser);
        });
        
        assertEquals("From card is not active", exception.getMessage());
        
        verify(cardRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void transferBetweenOwnCards_ToCardNotActive() {
        // Given
        toCard.setStatus(Card.CardStatus.BLOCKED);
        when(cardRepository.findByIdAndOwner(1L, testUser)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findByIdAndOwner(2L, testUser)).thenReturn(Optional.of(toCard));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transactionService.transferBetweenOwnCards(transferRequest, testUser);
        });
        
        assertEquals("To card is not active", exception.getMessage());
        
        verify(cardRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void transferBetweenOwnCards_NegativeAmount() {
        // Given
        transferRequest.setAmount(BigDecimal.valueOf(-100.00));
        when(cardRepository.findByIdAndOwner(1L, testUser)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findByIdAndOwner(2L, testUser)).thenReturn(Optional.of(toCard));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transactionService.transferBetweenOwnCards(transferRequest, testUser);
        });
        
        assertEquals("Transfer amount must be positive", exception.getMessage());
        
        verify(cardRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }
}