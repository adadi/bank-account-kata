package com.kata.bankaccount.adapter.persistence.jpa.entity;

import com.kata.bankaccount.domain.model.TransactionType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class TransactionEntity {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private AccountEntity account;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private Instant timestamp;

    @Column(name = "resulting_balance", nullable = false, precision = 19, scale = 2)
    private BigDecimal resultingBalance;

    public TransactionEntity() {}

    public TransactionEntity(UUID id, AccountEntity account, TransactionType type, BigDecimal amount, Instant timestamp, BigDecimal resultingBalance) {
        this.id = id;
        this.account = account;
        this.type = type;
        this.amount = amount;
        this.timestamp = timestamp;
        this.resultingBalance = resultingBalance;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public AccountEntity getAccount() { return account; }
    public void setAccount(AccountEntity account) { this.account = account; }

    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public BigDecimal getResultingBalance() { return resultingBalance; }
    public void setResultingBalance(BigDecimal resultingBalance) { this.resultingBalance = resultingBalance; }
}

