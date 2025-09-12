package com.bank.cards.repository;

import com.bank.cards.entity.Card;
import com.bank.cards.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    
    List<Card> findByOwner(User owner);
    
    List<Card> findByOwnerId(Long ownerId);
    
    Optional<Card> findByCardNumber(String cardNumber);
    
    Optional<Card> findByIdAndOwner(Long id, User owner);
    
    @Query("SELECT c FROM Card c WHERE c.owner.id = :userId AND c.status = 'ACTIVE'")
    List<Card> findActiveCardsByUserId(@Param("userId") Long userId);
    
    boolean existsByCardNumber(String cardNumber);
}