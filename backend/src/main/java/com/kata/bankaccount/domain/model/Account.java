package com.kata.bankaccount.domain.model;

import com.kata.bankaccount.domain.exception.InsufficientFundsException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Account {
    private final UUID id;
    private BigDecimal balance;
    private final List<Transaction> transactions;

    public Account(){
        this(UUID.randomUUID(), BigDecimal.ZERO);
    }
    public Account(UUID id, BigDecimal balance) {
        this.id = Objects.requireNonNull(id, "id");
        this.balance = Objects.requireNonNull(balance, "balance");
        this.transactions = new ArrayList<>();
    }

    public UUID getId() { return id; }
    public BigDecimal getBalance() { return balance; }
    public List<Transaction> getTransactions() { return Collections.unmodifiableList(transactions); }

    public void deposit(BigDecimal amount) {
        requirePositive(amount);
        balance = balance.add(amount);
        transactions.add(Transaction.deposit(amount, balance));
    }

    public void withdraw(BigDecimal amount) {
        requirePositive(amount);
        if (balance.compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds: balance=" + balance + ", requested=" + amount);
        }
        balance = balance.subtract(amount);
        transactions.add(Transaction.withdrawal(amount, balance));
    }

    private static void requirePositive(BigDecimal amount) {
        Objects.requireNonNull(amount, "amount");
        if (amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be > 0");
        }
    }
}

