package com.kata.bankaccount.application.ports.out;

import com.kata.bankaccount.domain.model.Account;

import java.util.UUID;

/**
 * Persistence port for Accounts. The implementation is expected to use a pessimistic lock in {@link #lockById(UUID)}.
 */
public interface AccountRepository {
    /**
     * Load the account by id with a pessimistic lock for update.
     */
    Account lockById(UUID accountId);

    /**
     * Persist the account's state (balance and transactions).
     */
    void save(Account account);

    /**
     * Load the account without acquiring a lock. Throws AccountNotFoundException when absent.
     */
    Account findById(UUID accountId);
}
