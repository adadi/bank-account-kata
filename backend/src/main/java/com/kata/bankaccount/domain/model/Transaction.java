package com.kata.bankaccount.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Immutable domain object for a single account operation.
 */
public class Transaction {
    /**
     * Unique identifier
     */
    private final UUID id;
    /**
     * Transaction type
     */
    private final TransactionType type;
    /**
     * Operation amount
     */
    private final BigDecimal amount;
    /**
     * Operation timestamp
     */
    private final Instant timestamp;
    /**
     * Balance after operation
     */
    private final BigDecimal resultingBalance;


    /**
     * Creates a transaction.
     * @param id               unique identifier
     * @param type             transaction type
     * @param amount           operation amount
     * @param timestamp        operation timestamp
     * @param resultingBalance balance after operation
     */
    private Transaction(UUID id, TransactionType type, BigDecimal amount, Instant timestamp, BigDecimal resultingBalance) {
        this.id = Objects.requireNonNull(id, "id");
        this.type = Objects.requireNonNull(type, "type");
        this.amount = Objects.requireNonNull(amount, "amount");
        this.timestamp = Objects.requireNonNull(timestamp, "timestamp");
        this.resultingBalance = Objects.requireNonNull(resultingBalance, "resultingBalance");
    }

    /**
     * Factory for a withdrawal transaction at current time.
     *
     * @param amount           amount withdrawn
     * @param resultingBalance balance after applying the withdrawal
     * @return new transaction
     */
    public static Transaction withdrawal(BigDecimal amount, BigDecimal resultingBalance) {
        return new Transaction(UUID.randomUUID(), TransactionType.WITHDRAWAL, amount, Instant.now(), resultingBalance);
    }

    /**
     * Factory for a deposit transaction at current time.
     *
     * @param amount           amount deposited
     * @param resultingBalance balance after applying the deposit
     * @return new transaction
     */
    public static Transaction deposit(BigDecimal amount, BigDecimal resultingBalance) {
        return new Transaction(UUID.randomUUID(), TransactionType.DEPOSIT, amount, Instant.now(), resultingBalance);
    }

    /**
     * Factory used by persistence adapters to rehydrate transactions.
     *
     * @param id               unique identifier
     * @param type             transaction type
     * @param amount           operation amount
     * @param timestamp        operation timestamp
     * @param resultingBalance balance after operation
     * @return rehydrated transaction
     */
    public static Transaction of(UUID id, TransactionType type, BigDecimal amount, Instant timestamp, BigDecimal resultingBalance) {
        return new Transaction(id, type, amount, timestamp, resultingBalance);
    }

    public UUID getId() {
        return id;
    }

    public TransactionType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public BigDecimal getResultingBalance() {
        return resultingBalance;
    }
}
