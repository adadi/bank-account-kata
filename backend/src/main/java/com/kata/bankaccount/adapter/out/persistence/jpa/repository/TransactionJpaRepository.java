package com.kata.bankaccount.adapter.out.persistence.jpa.repository;

import com.kata.bankaccount.adapter.out.persistence.jpa.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for transaction entities with convenience queries.
 */
public interface TransactionJpaRepository extends JpaRepository<TransactionEntity, UUID> {

    /**
     * Derived queries to avoid null-parameter type inference issues on PostgreSQL
     *
     * @param accountId the account ID
     * @return List of transactions for the account ordered by timestamp descending
     */
    List<TransactionEntity> findByAccount_IdOrderByTimestampDesc(UUID accountId);

    /**
     * Returns transactions for the given account from the given timestamp (inclusive) ordered by timestamp descending.
     *
     * @param accountId the account ID
     * @param from      the start timestamp (inclusive)
     * @return List of transactions for the account from the given timestamp ordered by timestamp descending
     */
    List<TransactionEntity> findByAccount_IdAndTimestampGreaterThanEqualOrderByTimestampDesc(UUID accountId, Instant from);

    /**
     * Returns transactions for the given account up to the given timestamp (inclusive) ordered by timestamp descending.
     *
     * @param accountId the account ID
     * @param to        the end timestamp (inclusive)
     * @return List of transactions for the account up to the given timestamp ordered by timestamp descending
     */
    List<TransactionEntity> findByAccount_IdAndTimestampLessThanEqualOrderByTimestampDesc(UUID accountId, Instant to);

    /**
     * Returns transactions for the given account between the given timestamps (inclusive) ordered by timestamp descending.
     *
     * @param accountId the account ID
     * @param from      the start timestamp (inclusive)
     * @param to        the end timestamp (inclusive)
     * @return List of transactions for the account between the given timestamps ordered by timestamp descending
     */
    List<TransactionEntity> findByAccount_IdAndTimestampBetweenOrderByTimestampDesc(UUID accountId, Instant from, Instant to);
}
