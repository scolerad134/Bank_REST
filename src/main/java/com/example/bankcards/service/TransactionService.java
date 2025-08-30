package com.example.bankcards.service;

import com.example.bankcards.dto.CreateTransactionRequest;
import com.example.bankcards.dto.TransactionDto;
import com.example.bankcards.entity.BankCard;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.Transaction;
import com.example.bankcards.entity.TransactionStatus;
import com.example.bankcards.repository.BankCardRepository;
import com.example.bankcards.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@Slf4j
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final BankCardRepository bankCardRepository;
    
    public TransactionService(TransactionRepository transactionRepository,
                            BankCardRepository bankCardRepository) {
        this.transactionRepository = transactionRepository;
        this.bankCardRepository = bankCardRepository;
    }
    
    public TransactionDto createTransaction(CreateTransactionRequest request, Long userId) {
        BankCard fromCard = bankCardRepository.findById(request.getFromCardId())
            .orElseThrow(() -> new RuntimeException("From card not found"));
        
        BankCard toCard = bankCardRepository.findById(request.getToCardId())
            .orElseThrow(() -> new RuntimeException("To card not found"));
        
        // Проверяем, что обе карты принадлежат пользователю
        if (!fromCard.getOwner().getId().equals(userId) || 
            !toCard.getOwner().getId().equals(userId)) {
            throw new RuntimeException("Access denied to one of the cards");
        }
        
        // Проверяем статус карт
        if (fromCard.getStatus() != CardStatus.ACTIVE || toCard.getStatus() != CardStatus.ACTIVE) {
            throw new RuntimeException("One of the cards is not active");
        }
        
        // Проверяем баланс
        if (fromCard.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient funds on card: " + fromCard.getId());
        }
        
        // Проверяем срок действия
        if (fromCard.getExpiryDate().isBefore(LocalDate.now()) || 
            toCard.getExpiryDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("One of the cards has expired");
        }
        
        // Создаем транзакцию
        Transaction transaction = Transaction.builder()
            .fromCard(fromCard)
            .toCard(toCard)
            .amount(request.getAmount())
            .status(TransactionStatus.PENDING)
            .description(request.getDescription())
            .build();
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        // Выполняем перевод
        try {
            fromCard.setBalance(fromCard.getBalance().subtract(request.getAmount()));
            toCard.setBalance(toCard.getBalance().add(request.getAmount()));
            
            bankCardRepository.save(fromCard);
            bankCardRepository.save(toCard);
            
            savedTransaction.setStatus(TransactionStatus.COMPLETED);
            transactionRepository.save(savedTransaction);
            
            log.info("Transaction completed: {} from card {} to card {}", 
                    request.getAmount(), fromCard.getId(), toCard.getId());
            
        } catch (Exception e) {
            savedTransaction.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(savedTransaction);
            log.error("Transaction failed: {}", e.getMessage());
            throw new RuntimeException("Transaction failed: " + e.getMessage());
        }
        
        return mapToDto(savedTransaction);
    }
    
    public TransactionDto getTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));
        return mapToDto(transaction);
    }
    
    public Page<TransactionDto> getUserTransactions(Long userId, Pageable pageable) {
        Page<Transaction> transactions = transactionRepository.findByUserId(userId, pageable);
        return transactions.map(this::mapToDto);
    }
    
    public Page<TransactionDto> getAllTransactions(Pageable pageable) {
        Page<Transaction> transactions = transactionRepository.findAll(pageable);
        return transactions.map(this::mapToDto);
    }
    
    public Page<TransactionDto> getTransactionsByStatus(TransactionStatus status, Pageable pageable) {
        Page<Transaction> transactions = transactionRepository.findByStatus(status, pageable);
        return transactions.map(this::mapToDto);
    }
    
    public Page<TransactionDto> getTransactionsByDateRange(LocalDateTime startDate, 
                                                          LocalDateTime endDate, 
                                                          Pageable pageable) {
        Page<Transaction> transactions = transactionRepository.findByDateRange(startDate, endDate, pageable);
        return transactions.map(this::mapToDto);
    }
    
    public List<Transaction> getTransactionsByUserId(Long userId) {
        return transactionRepository.findByFromCardOwnerIdOrToCardOwnerId(userId, userId);
    }
    
    public TransactionDto mapToDto(Transaction transaction) {
        return TransactionDto.builder()
            .id(transaction.getId())
            .fromCardMasked(transaction.getFromCard().getMaskedNumber())
            .toCardMasked(transaction.getToCard().getMaskedNumber())
            .amount(transaction.getAmount())
            .status(transaction.getStatus())
            .description(transaction.getDescription())
            .createdAt(transaction.getCreatedAt())
            .fromCardId(transaction.getFromCard().getId())
            .toCardId(transaction.getToCard().getId())
            .build();
    }
}
