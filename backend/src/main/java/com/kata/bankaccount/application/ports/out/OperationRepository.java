package com.kata.bankaccount.application.ports.out;

import java.util.UUID;

/**
 * Port for idempotency tracking of operations.
 */
public interface OperationRepository {
    /**
     * Checks whether the given operation id already exists.
     *
     * @param operationId idempotency key
     * @return true if operation id is already present
     */
    boolean exists(UUID operationId);

    /**
     * Persists the operation id if not present.
     *
     * @param operationId operationId key
     */
    void save(UUID operationId);
}
