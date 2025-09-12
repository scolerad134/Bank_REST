package com.bank.cards.service.impl;

import com.bank.cards.dto.TransferRequest;
import com.bank.cards.entity.Card;
import com.bank.cards.entity.Transaction;
import com.bank.cards.entity.User;
import com.bank.cards.repository.CardRepository;
import com.bank.cards.repository.TransactionRepository;
import com.bank.cards.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {
    
    private final TransactionRepository transactionRepository;
    private final CardRepository cardRepository;
    
    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository, 
                                  CardRepository cardRepository) {
        this.transactionRepository = transactionRepository;
        this.cardRepository = cardRepository;
    }
    
    @Override
    public Transaction transferBetweenOwnCards(TransferRequest transferRequest, User user) {
        Card fromCard = cardRepository.findByIdAndOwner(transferRequest.getFromCardId(), user)
                .orElseThrow(() -> new RuntimeException("From card not found"));
        
        Card toCard = cardRepository.findByIdAndOwner(transferRequest.getToCardId(), user)
                .orElseThrow(() -> new RuntimeException("To card not found"));
        
        if (fromCard.getStatus() != Card.CardStatus.ACTIVE) {
            throw new RuntimeException("From card is not active");
        }
        
        if (toCard.getStatus() != Card.CardStatus.ACTIVE) {
            throw new RuntimeException("To card is not active");
        }
        
        if (fromCard.getBalance().compareTo(transferRequest.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance");
        }
        
        if (transferRequest.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Transfer amount must be positive");
        }
        
        // Perform the transfer
        fromCard.setBalance(fromCard.getBalance().subtract(transferRequest.getAmount()));
        toCard.setBalance(toCard.getBalance().add(transferRequest.getAmount()));
        
        cardRepository.save(fromCard);
        cardRepository.save(toCard);
        
        // Create transaction record
        Transaction transaction = new Transaction(
            fromCard,
            toCard,
            transferRequest.getAmount(),
            Transaction.TransactionType.TRANSFER,
            transferRequest.getDescription()
        );
        transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
        
        return transactionRepository.save(transaction);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Transaction> getUserTransactions(User user, Pageable pageable) {
        return transactionRepository.findByUserId(user.getId(), pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Transaction> getCardTransactions(Long cardId, User user, Pageable pageable) {
        // Verify the card belongs to the user
        cardRepository.findByIdAndOwner(cardId, user)
                .orElseThrow(() -> new RuntimeException("Card not found"));
        
        return transactionRepository.findByCardId(cardId, pageable);
    }
}