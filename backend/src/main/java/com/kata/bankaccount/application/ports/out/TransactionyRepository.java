package com.kata.bankaccount.application.ports.out;

import com.kata.bankaccount.domain.model.Transaction;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Read port to fetch transactions (account statement).
 */
public interface TransactionyRepository {
    /**
     * Returns transactions for the given account, filtered by optional period, sorted by timestamp desc.
     */
    List<Transaction> findByAccountAndPeriod(UUID accountId, Instant from, Instant to);
}
