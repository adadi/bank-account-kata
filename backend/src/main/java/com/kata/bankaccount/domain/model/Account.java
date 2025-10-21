package com.kata.bankaccount.domain.model;

import com.kata.bankaccount.domain.exception.InsufficientFundsException;

import java.math.BigDecimal;
import java.util.*;

/**
 * Domain aggregate representing a bank account with a balance and its transactions.
 */
public class Account {
    /**
     * Unique identifier of the account
     */
    private final UUID id;
    /**
     * List of transactions associated with the account
     */
    private final List<Transaction> transactions;
    /**
     * Current balance of the account
     */
    private BigDecimal balance;

    /**
     * Creates a new account with a random id and zero balance.
     */
    public Account() {
        this(UUID.randomUUID(), BigDecimal.ZERO);
    }

    /**
     * Creates an account with the given id and initial balance.
     *
     * @param id      unique identifier of the account
     * @param balance initial balance of the account
     */
    public Account(UUID id, BigDecimal balance) {
        this.id = Objects.requireNonNull(id, "id");
        this.balance = Objects.requireNonNull(balance, "balance");
        this.transactions = new ArrayList<>();
    }

    /**
     * Ensures the amount is positive (> 0).
     *
     * @param amount amount to check
     * @throws IllegalArgumentException when amount &lt;= 0
     */
    private static void requirePositive(BigDecimal amount) {
        Objects.requireNonNull(amount, "amount");
        if (amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be > 0");
        }
    }

    /**
     * Deposits a positive amount and appends a transaction.
     *
     * @param amount amount to add (> 0)
     * @throws IllegalArgumentException when amount &lt;= 0
     */
    public void deposit(BigDecimal amount) {
        requirePositive(amount);
        balance = balance.add(amount);
        transactions.add(Transaction.deposit(amount, balance));
    }

    /**
     * Withdraws a positive amount if sufficient funds exist and appends a transaction.
     *
     * @param amount amount to subtract (> 0)
     * @throws IllegalArgumentException when amount &lt;= 0
     * @throws com.kata.bankaccount.domain.exception.InsufficientFundsException when balance is insufficient
     */
    public void withdraw(BigDecimal amount) {
        requirePositive(amount);
        if (balance.compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds: balance=" + balance + ", requested=" + amount);
        }
        balance = balance.subtract(amount);
        transactions.add(Transaction.withdrawal(amount, balance));
    }

    public UUID getId() {
        return id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }
}
