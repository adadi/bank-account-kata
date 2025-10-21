package com.kata.bankaccount.application.ports.out;

import com.kata.bankaccount.domain.model.Account;

import java.util.UUID;

/**
 * Persistence port for Accounts. The implementation is expected to use a pessimistic lock in {@link #lockById(UUID)}.
 */
public interface AccountRepository {
    /**
     * Load the account by id with a pessimistic lock for update.
     * @param accountId account identifier
     * @return locked account
     * Throws AccountNotFoundException when absent.
     */
    Account lockById(UUID accountId);

    /**
     * Persist the account's state (balance and transactions).
     * @param account account to save
     */
    void save(Account account);

    /**
     * Load the account without acquiring a lock. Throws AccountNotFoundException when absent.
     * @param accountId account identifier
     * @return account without lock
     */
    Account findById(UUID accountId);
}
