package com.kata.bankaccount.application.dto.response;

import com.kata.bankaccount.domain.model.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Read model for an account transaction used in statements.
 */
public record TransactionResponse(
        TransactionType type,
        BigDecimal amount,
        Instant timestamp,
        BigDecimal resultingBalance
) {}

