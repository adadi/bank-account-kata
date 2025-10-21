package com.kata.bankaccount.application.service;

import com.kata.bankaccount.application.ports.out.AccountRepository;
import com.kata.bankaccount.application.ports.out.OperationRepository;
import com.kata.bankaccount.domain.exception.InsufficientFundsException;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link AccountService#withdraw} covering happy path,
 * domain error propagation and idempotency.
 */
@ExtendWith(MockitoExtension.class)
class AccountServiceWithdrawTest {

    @Mock
    AccountRepository accountRepository;

    @Mock
    OperationRepository operationRepository;

    @InjectMocks
    AccountService accountService;

    @Captor
    ArgumentCaptor<Account> accountCaptor;

    UUID accountId;

    /** Creates a random account id for each test. */
    @BeforeEach
    void setUp() {
        accountId = UUID.randomUUID();
    }

    /**
     * Withdraw updates balance, saves account and persists the operation id, creating a WITHDRAWAL transaction.
     */
    @Test
    void withdraw_callsLockAndSave_updatesBalance_andCreatesTransaction_andPersistsOperation() {
        // Given
        var account = new Account(accountId, new BigDecimal("100.00"));
        when(accountRepository.lockById(accountId)).thenReturn(account);
        UUID operationId = UUID.randomUUID();
        when(operationRepository.exists(operationId)).thenReturn(false);

        // When
        accountService.withdraw(accountId, new BigDecimal("40.00"), operationId);

        // Then
        verify(accountRepository, times(1)).lockById(accountId);
        verify(accountRepository).save(accountCaptor.capture());

        var saved = accountCaptor.getValue();
        assertThat(saved.getBalance()).isEqualByComparingTo("60.00");
        assertThat(saved.getTransactions()).hasSize(1);
        assertThat(saved.getTransactions().get(0).getType()).isEqualTo(TransactionType.WITHDRAWAL);
        assertThat(saved.getTransactions().get(0).getAmount()).isEqualByComparingTo("40.00");
        verify(operationRepository).save(operationId);
    }

    /** Insufficient funds throws domain exception and nothing is saved. */
    @Test
    void withdraw_propagatesDomainException_andDoesNotSave() {
        // Given
        var account = new Account(accountId, new BigDecimal("10.00"));
        when(accountRepository.lockById(accountId)).thenReturn(account);
        UUID operationId = UUID.randomUUID();
        when(operationRepository.exists(operationId)).thenReturn(false);

        // When / Then
        assertThrows(InsufficientFundsException.class,
                () -> accountService.withdraw(accountId, new BigDecimal("40.00"), operationId));

        verify(accountRepository, times(1)).lockById(accountId);
        verify(accountRepository, never()).save(any());
        verify(operationRepository, never()).save(any());
    }

    /** Repeating the same operationId is idempotent and does not save again. */
    @Test
    void withdraw_sameOperationId_isIdempotent_noAdditionalSave() {
        // Given
        var account = new Account(accountId, new BigDecimal("100.00"));
        when(accountRepository.lockById(accountId)).thenReturn(account);
        UUID operationId = UUID.randomUUID();
        when(operationRepository.exists(operationId)).thenReturn(true);

        // When
        accountService.withdraw(accountId, new BigDecimal("40.00"), operationId);

        // Then
        verify(accountRepository, times(1)).lockById(accountId);
        verify(accountRepository, never()).save(any());
        verify(operationRepository, never()).save(any());
    }
}
