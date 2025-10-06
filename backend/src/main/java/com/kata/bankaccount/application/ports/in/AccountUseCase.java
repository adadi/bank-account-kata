package com.kata.bankaccount.application.ports.in;

import com.kata.bankaccount.domain.model.Account;

import java.math.BigDecimal;
import java.util.UUID;

public interface AccountUseCase {
    Account withdraw(UUID accountId, BigDecimal amount);
}

