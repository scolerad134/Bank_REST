package com.bank.cards.repository;

import com.bank.cards.entity.Card;
import com.bank.cards.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    List<Transaction> findByFromCardOrToCardOrderByCreatedAtDesc(Card fromCard, Card toCard);
    
    @Query("SELECT t FROM Transaction t LEFT JOIN FETCH t.fromCard LEFT JOIN FETCH t.toCard WHERE (t.fromCard IS NOT NULL AND t.fromCard.id = :cardId) OR (t.toCard IS NOT NULL AND t.toCard.id = :cardId) ORDER BY t.createdAt DESC")
    Page<Transaction> findByCardId(@Param("cardId") Long cardId, Pageable pageable);
    
    @Query("SELECT t FROM Transaction t LEFT JOIN FETCH t.fromCard LEFT JOIN FETCH t.toCard WHERE (t.fromCard IS NOT NULL AND t.fromCard.owner.id = :userId) OR (t.toCard IS NOT NULL AND t.toCard.owner.id = :userId) ORDER BY t.createdAt DESC")
    Page<Transaction> findByUserId(@Param("userId") Long userId, Pageable pageable);
}