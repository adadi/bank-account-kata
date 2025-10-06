package com.kata.bankaccount.domain.exception;

import java.util.UUID;

/**
 * Thrown when an account cannot be found in persistence.
 */
public class AccountNotFoundException extends RuntimeException {
    private final UUID accountId;
    private final UUID operationId;

    public AccountNotFoundException(UUID accountId) {
        super("Account not found: " + accountId);
        this.accountId = accountId;
        this.operationId = null;
    }

    public AccountNotFoundException(UUID accountId, UUID operationId) {
        super("Account not found: " + accountId);
        this.accountId = accountId;
        this.operationId = operationId;
    }

    public AccountNotFoundException(UUID accountId, UUID operationId, Throwable cause) {
        super("Account not found: " + accountId, cause);
        this.accountId = accountId;
        this.operationId = operationId;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public UUID getOperationId() {
        return operationId;
    }
}

