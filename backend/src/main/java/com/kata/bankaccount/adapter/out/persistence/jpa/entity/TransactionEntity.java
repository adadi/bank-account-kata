package com.kata.bankaccount.adapter.out.persistence.jpa.entity;

import com.kata.bankaccount.domain.model.TransactionType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * JPA entity representing a transaction line in an account statement.
 */
@Entity
@Table(name = "transactions")
public class TransactionEntity {
    /**
     * The transaction ID.
     */
    @Id
    private UUID id;

    /**
     * The account associated with the transaction.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private AccountEntity account;

    /**
     * The type of transaction (DEPOSIT or WITHDRAWAL).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    /**
     * The amount involved in the transaction.
     */
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    /**
     * The timestamp when the transaction occurred.
     */
    @Column(nullable = false)
    private Instant timestamp;

    /**
     * The resulting balance after the transaction.
     */
    @Column(name = "resulting_balance", nullable = false, precision = 19, scale = 4)
    private BigDecimal resultingBalance;

    /**
     * Default constructor for JPA.
     */
    public TransactionEntity() {
    }

    /**
     * Convenience constructor to create a fully-initialized entity.
     *
     * @param id               the transaction ID
     * @param account          the associated account
     * @param type             the transaction type
     * @param amount           the transaction amount
     * @param timestamp        the transaction timestamp
     * @param resultingBalance the resulting balance after the transaction
     */
    public TransactionEntity(UUID id, AccountEntity account, TransactionType type, BigDecimal amount, Instant timestamp, BigDecimal resultingBalance) {
        this.id = id;
        this.account = account;
        this.type = type;
        this.amount = amount;
        this.timestamp = timestamp;
        this.resultingBalance = resultingBalance;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public AccountEntity getAccount() {
        return account;
    }

    public void setAccount(AccountEntity account) {
        this.account = account;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public BigDecimal getResultingBalance() {
        return resultingBalance;
    }

    public void setResultingBalance(BigDecimal resultingBalance) {
        this.resultingBalance = resultingBalance;
    }
}
