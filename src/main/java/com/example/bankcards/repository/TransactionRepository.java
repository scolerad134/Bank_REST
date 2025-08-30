package com.example.bankcards.repository;

import com.example.bankcards.entity.Transaction;
import com.example.bankcards.entity.TransactionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByFromCardOwnerIdOrToCardOwnerId(Long fromOwnerId, Long toOwnerId);
    
    @Query("SELECT t FROM Transaction t WHERE " +
           "(t.fromCard.owner.id = :userId OR t.toCard.owner.id = :userId) " +
           "ORDER BY t.createdAt DESC")
    Page<Transaction> findByUserId(@Param("userId") Long userId, Pageable pageable);
    
    @Query("SELECT t FROM Transaction t WHERE t.status = :status")
    Page<Transaction> findByStatus(@Param("status") TransactionStatus status, Pageable pageable);
    
    @Query("SELECT t FROM Transaction t WHERE t.createdAt BETWEEN :startDate AND :endDate")
    Page<Transaction> findByDateRange(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        Pageable pageable
    );
    
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE " +
           "t.fromCard.owner.id = :userId AND t.status = 'COMPLETED' " +
           "AND t.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal getTotalTransfersAmount(
        @Param("userId") Long userId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    @Query("SELECT COUNT(t) FROM Transaction t WHERE " +
           "t.fromCard.owner.id = :userId AND t.status = 'COMPLETED'")
    long countSuccessfulTransactionsByUserId(@Param("userId") Long userId);
}
