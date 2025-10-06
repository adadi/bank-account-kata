package com.kata.bankaccount.adapter.out.persistence;

import com.kata.bankaccount.adapter.out.persistence.jpa.repository.TransactionJpaRepository;
import com.kata.bankaccount.application.dto.response.TransactionResponse;
import com.kata.bankaccount.application.ports.out.TransactionyRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
@Transactional(readOnly = true)
public class TransactionRepositoryAdapter implements TransactionyRepository {

    private final TransactionJpaRepository jpaRepository;

    public TransactionRepositoryAdapter(TransactionJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<TransactionResponse> findByAccountAndPeriod(UUID accountId, Instant from, Instant to) {
        return jpaRepository.findByAccountAndPeriodOrderByTimestampDesc(accountId, from, to)
                .stream()
                .map(e -> new TransactionResponse(
                        e.getType(),
                        e.getAmount(),
                        e.getTimestamp(),
                        e.getResultingBalance()
                ))
                .toList();
    }
}

