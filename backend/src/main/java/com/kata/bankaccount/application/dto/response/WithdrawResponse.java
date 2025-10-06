package com.kata.bankaccount.application.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Application-level response for a withdrawal operation.
 */
public record WithdrawResponse(UUID accountId, BigDecimal balance) {}

