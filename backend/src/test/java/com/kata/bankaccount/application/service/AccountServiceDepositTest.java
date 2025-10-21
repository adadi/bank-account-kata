package com.kata.bankaccount.application.service;

import com.kata.bankaccount.application.dto.response.DepositResponse;
import com.kata.bankaccount.application.ports.out.AccountRepository;
import com.kata.bankaccount.application.ports.out.OperationRepository;
import com.kata.bankaccount.application.ports.out.TransactionRepository;
import com.kata.bankaccount.domain.model.Account;
import com.kata.bankaccount.domain.model.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link AccountService#deposit} ensuring idempotency,
 * domain validation and persistence interactions.
 */
@ExtendWith(MockitoExtension.class)
class AccountServiceDepositTest {

    @Mock AccountRepository accountRepository;
    @Mock
    OperationRepository operationRepository;
    @Mock
    TransactionRepository transactionRepository;

    @InjectMocks
    AccountService accountService;

    @Captor ArgumentCaptor<Account> accountCaptor;

    UUID accountId;
    UUID operationId;

    /** Initializes random ids for each test. */
    @BeforeEach
    void setUp() {
        accountId = UUID.randomUUID();
        operationId = UUID.randomUUID();
    }

    /** New deposit is applied and returns applied=true. */
    @Test
    void deposit_positiveAmount_returnsAppliedTrue() {
        // Given
        var account = new Account(accountId, new BigDecimal("0"));
        when(accountRepository.lockById(accountId)).thenReturn(account);
        when(operationRepository.exists(operationId)).thenReturn(false);

        // When
        DepositResponse result = accountService.deposit(accountId, new BigDecimal("50"), operationId);

        // Then
        assertEquals(new BigDecimal("50"), result.balance());
        assertThat(result.applied()).isTrue();
        verify(accountRepository, times(1)).lockById(accountId);
    }

    /** Deposit persists account and adds a DEPOSIT transaction with correct details. */
    @Test
    void deposit_positiveAmount_saves_andCreatesTransaction_details() {
        // Given
        var account = new Account(accountId, new BigDecimal("0"));
        when(accountRepository.lockById(accountId)).thenReturn(account);
        when(operationRepository.exists(operationId)).thenReturn(false);

        // When
        accountService.deposit(accountId, new BigDecimal("50.00"), operationId);

        // Then
        verify(accountRepository).save(accountCaptor.capture());
        var saved = accountCaptor.getValue();
        assertThat(saved.getBalance()).isEqualByComparingTo("50.00");
        assertThat(saved.getTransactions()).hasSize(1);
        assertThat(saved.getTransactions().get(0).getType()).isEqualTo(TransactionType.DEPOSIT);
        assertThat(saved.getTransactions().get(0).getAmount()).isEqualByComparingTo("50.00");
        verify(operationRepository).save(operationId);
    }

    /** Non-positive amount is rejected and nothing is saved. */
    @Test
    void deposit_nonPositiveAmount_throws_andDoesNotSave() {
        // Given
        var account = new Account(accountId, new BigDecimal("0"));
        when(accountRepository.lockById(accountId)).thenReturn(account);
        when(operationRepository.exists(operationId)).thenReturn(false);

        // When / Then
        assertThrows(IllegalArgumentException.class,
                () -> accountService.deposit(accountId, new BigDecimal("0"), operationId));

        verify(accountRepository, never()).save(any());
        verify(operationRepository, never()).save(any());
    }

    /** Repeating the same operationId is idempotent and returns applied=false. */
    @Test
    void deposit_sameOperationId_isIdempotent_noAdditionalSave_andReturnsAppliedFalse() {
        // Given
        when(operationRepository.exists(operationId)).thenReturn(true);
        var account = new Account(accountId, new BigDecimal("10"));
        when(accountRepository.lockById(accountId)).thenReturn(account);

        // When
        var result = accountService.deposit(accountId, new BigDecimal("50"), operationId);

        // Then
        assertEquals(new BigDecimal("10"), result.balance());
        assertThat(result.applied()).isFalse();
        verify(accountRepository, never()).save(any());
        verify(operationRepository, never()).save(any());
    }
}
