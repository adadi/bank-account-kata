package com.kata.bankaccount.domain.exception;

/**
 * Thrown when a withdrawal exceeds the available balance.
 */
public class InsufficientFundsException extends IllegalStateException {
    public InsufficientFundsException(String message) {
        super(message);
    }
}

