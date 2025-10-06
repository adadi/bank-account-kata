package com.kata.bankaccount.application.ports.in;

import com.kata.bankaccount.application.dto.response.DepositResponse;

import java.math.BigDecimal;
import java.util.UUID;

public interface DepositUseCase {

    /**
     * Deposit the given amount into the account with an idempotency key.
     * Returns a DepositResponse containing the updated balance and whether the operation was applied
     * (true = newly applied → 201 Created; false = idempotent/no-op → 200 OK).
     */
    DepositResponse deposit(UUID accountId, BigDecimal amount, UUID operationId);

}
