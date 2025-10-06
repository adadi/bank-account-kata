package com.kata.bankaccount.domaine.model;

import java.math.BigDecimal;
import java.util.Objects;

public class Account {

    private BigDecimal balance = BigDecimal.ZERO;

    public BigDecimal getBalance() {
        return balance;
    }

    public void deposit(BigDecimal amount) {
        validatePositive(amount, "Deposit amount must be > 0");
        balance = balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        validatePositive(amount, "Withdrawal amount must be > 0");
        if (balance.compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient funds");
        }
        balance = balance.subtract(amount);
    }

    private static void validatePositive(BigDecimal amount, String message) {
        Objects.requireNonNull(amount, "Amount cannot be null");
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(message);
        }
    }
}

