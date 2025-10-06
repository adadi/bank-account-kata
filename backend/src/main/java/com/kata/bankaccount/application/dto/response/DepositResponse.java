package com.kata.bankaccount.application.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Application-level response for a deposit operation.
 * Contains whether the deposit was applied (for 201 vs 200).
 */
public record DepositResponse(UUID accountId, BigDecimal balance, boolean applied) {}

