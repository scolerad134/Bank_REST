package com.example.bankcards.repository;

import com.example.bankcards.entity.BankCard;
import com.example.bankcards.entity.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BankCardRepository extends JpaRepository<BankCard, Long> {
    List<BankCard> findByOwnerId(Long ownerId);
    List<BankCard> findByOwnerIdAndStatus(Long ownerId, CardStatus status);
    Optional<BankCard> findByCardNumber(String cardNumber);
    
    @Query("SELECT c FROM BankCard c WHERE c.owner.id = :ownerId " +
           "AND (:status IS NULL OR c.status = :status) " +
           "AND (:searchTerm IS NULL OR c.cardholderName LIKE %:searchTerm%)")
    Page<BankCard> findByOwnerIdWithFilters(
        @Param("ownerId") Long ownerId,
        @Param("status") CardStatus status,
        @Param("searchTerm") String searchTerm,
        Pageable pageable
    );
    
    @Query("SELECT c FROM BankCard c WHERE c.expiryDate < :currentDate")
    List<BankCard> findExpiredCards(@Param("currentDate") LocalDate currentDate);
}
