package com.kata.bankaccount.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccountTest {

    @Test
    @DisplayName("deposit(100) → balance = 100")
    void depositPositiveAmountUpdatesBalance() {
        Account account = new Account();

        account.deposit(new BigDecimal("100"));

        assertThat(account.getBalance()).isEqualByComparingTo("100");
    }

    @Test
    @DisplayName("deposit(-1) → IllegalArgumentException")
    void depositNegativeAmountThrows() {
        Account account = new Account();

        assertThatThrownBy(() -> account.deposit(new BigDecimal("-1")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("withdraw(30) after deposit(100) → balance = 70")
    void withdrawAfterDepositUpdatesBalance() {
        Account account = new Account();
        account.deposit(new BigDecimal("100"));

        account.withdraw(new BigDecimal("30"));

        assertThat(account.getBalance()).isEqualByComparingTo("70");
    }

    @Test
    @DisplayName("withdraw(200) with balance 100 → IllegalStateException")
    void withdrawMoreThanBalanceThrows() {
        Account account = new Account();
        account.deposit(new BigDecimal("100"));

        assertThatThrownBy(() -> account.withdraw(new BigDecimal("200")))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("withdraw(0) → IllegalArgumentException")
    void withdrawNonPositiveAmountThrows() {
        Account account = new Account();

        assertThatThrownBy(() -> account.withdraw(new BigDecimal("0")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Precision: deposit(0.10) ten times → balance = 1.00")
    void depositOneTenthTenTimesEqualsOne() {
        Account account = new Account();

        for (int i = 0; i < 10; i++) {
            account.deposit(new BigDecimal("0.10"));
        }

        assertThat(account.getBalance()).isEqualByComparingTo("1.00");
    }

    @Test
    @DisplayName("Given balance = 100, When I withdraw 30, Then balance = 70")
    void givenWhenThen_withdrawScenario() {
        // Given
        Account account = new Account();
        account.deposit(new BigDecimal("100"));

        // When
        account.withdraw(new BigDecimal("30"));

        // Then
        assertThat(account.getBalance()).isEqualByComparingTo("70");
    }
}
