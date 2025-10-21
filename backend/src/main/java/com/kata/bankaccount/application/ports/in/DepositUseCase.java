package com.kata.bankaccount.application.ports.in;

import com.kata.bankaccount.application.dto.response.DepositResponse;

import java.math.BigDecimal;
import java.util.UUID;
/**
 * Use case for depositing money into an account.
 */
public interface DepositUseCase {

    /**
     * Deposit the given amount into the account with an idempotency key.
     * Returns a DepositResponse containing the updated balance and whether the operation was applied
     * (true = newly applied → 201 Created; false = idempotent/no-op → 200 OK).
     * @param accountId account identifier
     * @param amount positive amount to deposit
     * @param operationId idempotency key
     * @return updated balance and whether it was applied (idempotency)
     * @throws com.kata.bankaccount.domain.exception.AccountNotFoundException when account is missing
     */
    DepositResponse deposit(UUID accountId, BigDecimal amount, UUID operationId);

}
