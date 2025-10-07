package com.kata.bankaccount.adapter.out.persistence.jpa.repository;

import com.kata.bankaccount.adapter.out.persistence.jpa.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface TransactionJpaRepository extends JpaRepository<TransactionEntity, UUID> {

    // Derived queries to avoid null-parameter type inference issues on PostgreSQL
    List<TransactionEntity> findByAccount_IdOrderByTimestampDesc(UUID accountId);

    List<TransactionEntity> findByAccount_IdAndTimestampGreaterThanEqualOrderByTimestampDesc(UUID accountId, Instant from);

    List<TransactionEntity> findByAccount_IdAndTimestampLessThanEqualOrderByTimestampDesc(UUID accountId, Instant to);

    List<TransactionEntity> findByAccount_IdAndTimestampBetweenOrderByTimestampDesc(UUID accountId, Instant from, Instant to);
}
