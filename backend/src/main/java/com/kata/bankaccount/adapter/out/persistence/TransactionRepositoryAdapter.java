package com.kata.bankaccount.adapter.out.persistence;

import com.kata.bankaccount.adapter.out.persistence.jpa.repository.TransactionJpaRepository;
import com.kata.bankaccount.application.ports.out.TransactionRepository;
import com.kata.bankaccount.domain.model.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * JPA-based read adapter for account transactions.
 */
@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TransactionRepositoryAdapter implements TransactionRepository {

    private final TransactionJpaRepository jpaRepository;

    @Override
    public List<Transaction> findByAccountAndPeriod(UUID accountId, Instant from, Instant to) {
        var entities = (from == null && to == null)
                ? jpaRepository.findByAccount_IdOrderByTimestampDesc(accountId)
                : (from == null)
                    ? jpaRepository.findByAccount_IdAndTimestampLessThanEqualOrderByTimestampDesc(accountId, to)
                    : (to == null)
                        ? jpaRepository.findByAccount_IdAndTimestampGreaterThanEqualOrderByTimestampDesc(accountId, from)
                        : jpaRepository.findByAccount_IdAndTimestampBetweenOrderByTimestampDesc(accountId, from, to);

        return entities
                .stream()
                .map(e -> Transaction.of(
                        e.getId(),
                        e.getType(),
                        e.getAmount(),
                        e.getTimestamp(),
                        e.getResultingBalance()
                ))
                .toList();
    }
}
