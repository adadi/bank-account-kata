package com.kata.bankaccount.application.service;

import com.kata.bankaccount.application.dto.response.TransactionResponse;
import com.kata.bankaccount.application.ports.out.AccountRepository;
import com.kata.bankaccount.application.ports.out.OperationRepository;
import com.kata.bankaccount.application.ports.out.TransactionyRepository;
import com.kata.bankaccount.domain.exception.AccountNotFoundException;
import com.kata.bankaccount.domain.model.Account;
import com.kata.bankaccount.domain.model.Transaction;
import com.kata.bankaccount.domain.model.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTransactionsTest {

    @Mock AccountRepository accountRepository;
    @Mock OperationRepository operationRepository;
    @Mock TransactionyRepository transactionyRepository;

    @InjectMocks AccountService service;

    UUID accountId;

    @BeforeEach
    void setUp() {
        accountId = UUID.randomUUID();
    }

    @Test
    void transactions_callsLockAndRepo_andMapsToResponse() {
        // Given
        when(accountRepository.lockById(accountId)).thenReturn(new Account(accountId, BigDecimal.ZERO));

        Instant from = Instant.parse("2024-01-01T00:00:00Z");
        Instant to = Instant.parse("2024-01-03T00:00:00Z");

        var t1 = Transaction.of(UUID.randomUUID(), TransactionType.DEPOSIT,
                new BigDecimal("100.00"), Instant.parse("2024-01-01T10:00:00Z"), new BigDecimal("100.00"));
        var t2 = Transaction.of(UUID.randomUUID(), TransactionType.WITHDRAWAL,
                new BigDecimal("30.00"), Instant.parse("2024-01-02T11:00:00Z"), new BigDecimal("70.00"));

        when(transactionyRepository.findByAccountAndPeriod(accountId, from, to))
                .thenReturn(List.of(t2, t1)); // already sorted desc by repo

        // When
        List<TransactionResponse> res = service.transactions(accountId, from, to);

        // Then
        verify(accountRepository, times(1)).lockById(accountId);
        verify(transactionyRepository, times(1)).findByAccountAndPeriod(accountId, from, to);

        assertThat(res).hasSize(2);
        assertThat(res.get(0).type()).isEqualTo(TransactionType.WITHDRAWAL);
        assertThat(res.get(0).amount()).isEqualByComparingTo("30.00");
        assertThat(res.get(0).timestamp()).isEqualTo(Instant.parse("2024-01-02T11:00:00Z"));
        assertThat(res.get(0).resultingBalance()).isEqualByComparingTo("70.00");

        assertThat(res.get(1).type()).isEqualTo(TransactionType.DEPOSIT);
        assertThat(res.get(1).amount()).isEqualByComparingTo("100.00");
        assertThat(res.get(1).timestamp()).isEqualTo(Instant.parse("2024-01-01T10:00:00Z"));
        assertThat(res.get(1).resultingBalance()).isEqualByComparingTo("100.00");
    }

    @Test
    void transactions_withNullPeriod_forwardsNullsToRepo_andReturnsEmptyList() {
        // Given
        when(accountRepository.lockById(accountId)).thenReturn(new Account(accountId, BigDecimal.ZERO));
        when(transactionyRepository.findByAccountAndPeriod(any(), any(), any())).thenReturn(List.of());

        // When
        List<TransactionResponse> res = service.transactions(accountId, null, null);

        // Then
        verify(transactionyRepository).findByAccountAndPeriod(eq(accountId), isNull(), isNull());
        assertThat(res).isEmpty();
    }

    @Test
    void transactions_missingAccount_throwsAccountNotFound() {
        // Given
        when(accountRepository.lockById(accountId)).thenThrow(new AccountNotFoundException(accountId));

        // When / Then
        assertThrows(AccountNotFoundException.class, () -> service.transactions(accountId, null, null));
        verify(transactionyRepository, never()).findByAccountAndPeriod(any(), any(), any());
    }
}

