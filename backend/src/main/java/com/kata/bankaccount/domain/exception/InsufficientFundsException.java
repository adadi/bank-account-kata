package com.kata.bankaccount.domain.exception;

/**
 * Thrown when a withdrawal exceeds the available balance.
 */
public class InsufficientFundsException extends IllegalStateException {
    /**
     * Constructs a new InsufficientFundsException with the specified detail message.
     *
     * @param message the detail message
     */
    public InsufficientFundsException(String message) {
        super(message);
    }
}

