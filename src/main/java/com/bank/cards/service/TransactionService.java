package com.bank.cards.service;

import com.bank.cards.dto.TransferRequest;
import com.bank.cards.entity.Transaction;
import com.bank.cards.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransactionService {
    
    Transaction transferBetweenOwnCards(TransferRequest transferRequest, User user);
    
    Page<Transaction> getUserTransactions(User user, Pageable pageable);
    
    Page<Transaction> getCardTransactions(Long cardId, User user, Pageable pageable);
}