package com.kata.bankaccount.adapter.out.persistence.jpa.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * JPA entity for persisted accounts.
 */
@Entity
@Table(name = "accounts")
public class AccountEntity {
    /**
     * The account ID.
     */
    @Id
    private UUID id;
    /**
     * The account balance.
     */
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal balance;
    /**
     * The list of transactions associated with the account.
     */
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<TransactionEntity> transactions = new ArrayList<>();

    /**
     * Default constructor for JPA.
     */
    public AccountEntity() {
    }

    /**
     * Convenience constructor used by adapters.
     *
     * @param id      the account ID
     * @param balance the account balance
     */
    public AccountEntity(UUID id, BigDecimal balance) {
        this.id = id;
        this.balance = balance;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public List<TransactionEntity> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionEntity> transactions) {
        this.transactions = transactions;
    }
}
