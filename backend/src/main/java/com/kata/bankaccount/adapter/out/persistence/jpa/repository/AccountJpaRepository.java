package com.kata.bankaccount.adapter.out.persistence.jpa.repository;

import com.kata.bankaccount.adapter.out.persistence.jpa.entity.AccountEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface AccountJpaRepository extends JpaRepository<AccountEntity, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from AccountEntity a where a.id = :id")
    Optional<AccountEntity> lockById(@Param("id") UUID id);

    @Query("select a from AccountEntity a left join fetch a.transactions where a.id = :id")
    Optional<AccountEntity> findByIdWithTransactions(@Param("id") UUID id);
}

