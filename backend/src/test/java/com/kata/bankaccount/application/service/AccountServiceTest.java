package com.kata.bankaccount.application.service;

import com.kata.bankaccount.application.ports.out.AccountRepository;
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

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    AccountRepository accountRepository;

    @InjectMocks
    AccountService accountService;

    @Captor
    ArgumentCaptor<Account> accountCaptor;

    UUID accountId;

    @BeforeEach
    void setUp() {
        accountId = UUID.randomUUID();
    }

    @Test
    void withdraw_callsLockAndSave_updatesBalance_andCreatesTransaction() {
        // Given
        var account = new Account(accountId, new BigDecimal("100.00"));
        when(accountRepository.lockById(accountId)).thenReturn(account);

        // When
        accountService.withdraw(accountId, new BigDecimal("40.00"));

        // Then
        verify(accountRepository, times(1)).lockById(accountId);
        verify(accountRepository).save(accountCaptor.capture());

        var saved = accountCaptor.getValue();
        assertThat(saved.getBalance()).isEqualByComparingTo("60.00");
        assertThat(saved.getTransactions()).hasSize(1);
        assertThat(saved.getTransactions().get(0).getType()).isEqualTo(TransactionType.WITHDRAWAL);
        assertThat(saved.getTransactions().get(0).getAmount()).isEqualByComparingTo("40.00");
    }

    @Test
    void withdraw_propagatesDomainException_andDoesNotSave() {
        // Given
        var account = new Account(accountId, new BigDecimal("10.00"));
        when(accountRepository.lockById(accountId)).thenReturn(account);

        // When / Then
        assertThrows(InsufficientFundsException.class,
                () -> accountService.withdraw(accountId, new BigDecimal("40.00")));

        verify(accountRepository, times(1)).lockById(accountId);
        verify(accountRepository, never()).save(any());
    }
}

