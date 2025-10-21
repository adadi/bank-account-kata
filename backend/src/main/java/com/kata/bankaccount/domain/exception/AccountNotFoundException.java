package com.kata.bankaccount.domain.exception;

import java.util.UUID;

/**
 * Thrown when an account cannot be found in persistence.
 */
public class AccountNotFoundException extends RuntimeException {
    private final UUID accountId;
    private final UUID operationId;

    /**
     * Constructs a new AccountNotFoundException with the specified account ID.
     *
     * @param accountId the ID of the account that was not found
     */
    public AccountNotFoundException(UUID accountId) {
        super("Account not found: " + accountId);
        this.accountId = accountId;
        this.operationId = null;
    }

    /**
     * Constructs a new AccountNotFoundException with the specified account ID and operation ID.
     *
     * @param accountId the ID of the account that was not found
     * @param operationId the ID of the operation that triggered the exception
     */
    public AccountNotFoundException(UUID accountId, UUID operationId) {
        super("Account not found: " + accountId);
        this.accountId = accountId;
        this.operationId = operationId;
    }


    /**
     * Constructs a new AccountNotFoundException with the specified account ID, operation ID, and cause.
     *
     * @param accountId the ID of the account that was not found
     * @param operationId the ID of the operation that triggered the exception
     * @param cause the cause of the exception
     */
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

