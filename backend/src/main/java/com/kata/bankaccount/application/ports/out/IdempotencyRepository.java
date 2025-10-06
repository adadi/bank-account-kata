package com.kata.bankaccount.application.ports.out;

import java.util.UUID;

/**
 * Port for idempotency tracking of operations.
 */
public interface IdempotencyRepository {
    boolean exists(UUID operationId);
    void save(UUID operationId);
}

