package com.kata.bankaccount.application.ports.in;

import com.kata.bankaccount.application.dto.response.AccountResponse;

import java.util.UUID;

public interface GetAccountUseCase {
    AccountResponse get(UUID accountId);
}

