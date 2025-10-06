package com.kata.bankaccount.application.ports.in;

import com.kata.bankaccount.application.dto.response.DepositResponse;
import com.kata.bankaccount.application.dto.response.WithdrawResponse;

import java.math.BigDecimal;
import java.util.UUID;

public interface AccountUseCase {
    /**
     * Withdraw the given amount with an idempotency key.
     * Subsequent calls with the same operationId must not apply twice.
     */
    WithdrawResponse withdraw(UUID accountId, BigDecimal amount, UUID operationId);

    /**
     * Deposit the given amount into the account with an idempotency key.
     * Returns a DepositResponse containing the updated balance and whether the operation was applied
     * (true = newly applied → 201 Created; false = idempotent/no-op → 200 OK).
     */
    DepositResponse deposit(UUID accountId, BigDecimal amount, UUID operationId);
}
