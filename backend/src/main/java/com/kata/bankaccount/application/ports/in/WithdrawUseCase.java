package com.kata.bankaccount.application.ports.in;

import com.kata.bankaccount.application.dto.response.WithdrawResponse;

import java.math.BigDecimal;
import java.util.UUID;

public interface WithdrawUseCase {
    /**
     * Withdraw the given amount with an idempotency key.
     * Subsequent calls with the same operationId must not apply twice.
     */
    WithdrawResponse withdraw(UUID accountId, BigDecimal amount, UUID operationId);

    }
