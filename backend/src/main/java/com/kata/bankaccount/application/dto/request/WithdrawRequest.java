package com.kata.bankaccount.application.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Request payload for withdrawal endpoint.
 */
public record WithdrawRequest(
        @NotNull(message = "amount is required")
        @DecimalMin(value = "0.01", message = "amount must be > 0")
        BigDecimal amount,
        @NotNull(message = "operationId is required")
        UUID operationId
) {}
