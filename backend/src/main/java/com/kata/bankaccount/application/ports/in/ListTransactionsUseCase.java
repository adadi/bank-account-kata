package com.kata.bankaccount.application.ports.in;

import com.kata.bankaccount.application.dto.response.TransactionResponse;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
/**
 * Use case for listing account transactions.
 */
public interface ListTransactionsUseCase {

    /**
     * List transactions of an account, optionally filtered by [from, to], sorted by timestamp desc.
     * @param accountId account identifier
     * @param from inclusive start timestamp (optional)
     * @param to inclusive end timestamp (optional)
     * @return transactions sorted by timestamp desc
     */
    List<TransactionResponse> transactions(UUID accountId, Instant from, Instant to);
}
