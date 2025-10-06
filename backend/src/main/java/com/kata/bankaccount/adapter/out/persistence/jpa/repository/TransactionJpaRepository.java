package com.kata.bankaccount.adapter.out.persistence.jpa.repository;

import com.kata.bankaccount.adapter.out.persistence.jpa.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface TransactionJpaRepository extends JpaRepository<TransactionEntity, UUID> {

    @Query("select t from TransactionEntity t " +
            "where t.account.id = :accountId " +
            "and (:from is null or t.timestamp >= :from) " +
            "and (:to is null or t.timestamp <= :to) " +
            "order by t.timestamp desc")
    List<TransactionEntity> findByAccountAndPeriodOrderByTimestampDesc(
            @Param("accountId") UUID accountId,
            @Param("from") Instant from,
            @Param("to") Instant to);
}

