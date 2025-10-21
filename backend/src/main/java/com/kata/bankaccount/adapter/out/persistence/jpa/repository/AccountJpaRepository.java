package com.kata.bankaccount.adapter.out.persistence.jpa.repository;

import com.kata.bankaccount.adapter.out.persistence.jpa.entity.AccountEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for accounts with explicit lock queries.
 */
public interface AccountJpaRepository extends JpaRepository<AccountEntity, UUID> {

    /**
     * Acquire a pessimistic write lock on the account row.
     *
     * @param id the account ID
     * @return the locked account entity
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from AccountEntity a where a.id = :id")
    Optional<AccountEntity> lockById(@Param("id") UUID id);

    /**
     * Fetch the account with its transactions to append new items.
     *
     * @param id the account ID
     * @return the account entity with transactions
     */
    @Query("select a from AccountEntity a left join fetch a.transactions where a.id = :id")
    Optional<AccountEntity> findByIdWithTransactions(@Param("id") UUID id);
}
