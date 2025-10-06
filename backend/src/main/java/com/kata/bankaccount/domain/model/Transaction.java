package com.kata.bankaccount.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Transaction {
    private final UUID id;
    private final TransactionType type;
    private final BigDecimal amount;
    private final Instant timestamp;
    private final BigDecimal resultingBalance;

    private Transaction(UUID id, TransactionType type, BigDecimal amount, Instant timestamp, BigDecimal resultingBalance) {
        this.id = Objects.requireNonNull(id, "id");
        this.type = Objects.requireNonNull(type, "type");
        this.amount = Objects.requireNonNull(amount, "amount");
        this.timestamp = Objects.requireNonNull(timestamp, "timestamp");
        this.resultingBalance = Objects.requireNonNull(resultingBalance, "resultingBalance");
    }

    public static Transaction withdrawal(BigDecimal amount, BigDecimal resultingBalance) {
        return new Transaction(UUID.randomUUID(), TransactionType.WITHDRAWAL, amount, Instant.now(), resultingBalance);
    }

    public static Transaction deposit(BigDecimal amount, BigDecimal resultingBalance) {
        return new Transaction(UUID.randomUUID(), TransactionType.DEPOSIT, amount, Instant.now(), resultingBalance);
    }

    public UUID getId() { return id; }
    public TransactionType getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public Instant getTimestamp() { return timestamp; }
    public BigDecimal getResultingBalance() { return resultingBalance; }
}

