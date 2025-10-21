package com.kata.bankaccount.application.ports.in;

import com.kata.bankaccount.application.dto.response.AccountResponse;

import java.util.UUID;

/** Input port to retrieve a basic account projection. */
public interface GetAccountUseCase {
    /** Returns id and balance for the given account.
     * Returns a read projection of the account.
     * @param accountId account identifier
     * @return id and current balance
     */
    AccountResponse getAccountById(UUID accountId);
}
