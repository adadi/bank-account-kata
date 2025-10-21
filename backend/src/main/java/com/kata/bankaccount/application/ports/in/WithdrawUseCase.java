package com.kata.bankaccount.application.ports.in;

import com.kata.bankaccount.application.dto.response.WithdrawResponse;

import java.math.BigDecimal;
import java.util.UUID;
/**
 * Use case for withdrawing money from an account.
 */
public interface WithdrawUseCase {
    /**
     * Withdraw the given amount with an operationId key.
     * Later calls with the same operationId must not apply twice.
     * @param accountId account identifier
     * @param amount positive amount to withdraw
     * @param operationId idempotency key
     * @return updated balance response
     * @throws com.kata.bankaccount.domain.exception.AccountNotFoundException when account is missing
     * @throws com.kata.bankaccount.domain.exception.InsufficientFundsException when balance is insufficient
     */
    WithdrawResponse withdraw(UUID accountId, BigDecimal amount, UUID operationId);

    }
