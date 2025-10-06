package com.kata.bankaccount.application.ports.in;

import com.kata.bankaccount.application.dto.response.TransactionResponse;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ListTransactionsUseCase {

    /**
     * List transactions of an account, optionally filtered by [from, to], sorted by timestamp desc.
     */
    List<TransactionResponse> transactions(UUID accountId, Instant from, Instant to);
}
