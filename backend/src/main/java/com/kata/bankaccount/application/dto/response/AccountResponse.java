package com.kata.bankaccount.application.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Basic account projection for read endpoints.
 */
public record AccountResponse(UUID accountId, BigDecimal balance) {}

