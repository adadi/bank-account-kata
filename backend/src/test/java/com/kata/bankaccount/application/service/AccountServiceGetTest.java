package com.kata.bankaccount.application.service;

import com.kata.bankaccount.application.dto.response.AccountResponse;
import com.kata.bankaccount.application.ports.out.AccountRepository;
import com.kata.bankaccount.application.ports.out.OperationRepository;
import com.kata.bankaccount.application.ports.out.TransactionRepository;
import com.kata.bankaccount.domain.exception.AccountNotFoundException;
import com.kata.bankaccount.domain.model.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link AccountService#getAccountById} validating the returned projection
 * and behavior when the account is missing.
 */
class AccountServiceGetTest {

    private AccountRepository accountRepository;
    private OperationRepository operationRepository;
    private TransactionRepository transactionRepository;
    private AccountService service;

    /** Creates service with mocked repositories before each test. */
    @BeforeEach
    void setUp() {
        accountRepository = Mockito.mock(AccountRepository.class);
        operationRepository = Mockito.mock(OperationRepository.class);
        transactionRepository = Mockito.mock(TransactionRepository.class);
        service = new AccountService(accountRepository, operationRepository, transactionRepository);
    }

    /** Existing account returns id and balance. */
    @Test
    void get_AccountById_returnsAccountResponse_whenAccountExists() {
        UUID id = UUID.randomUUID();
        BigDecimal balance = new BigDecimal("123.45");
        when(accountRepository.findById(id)).thenReturn(new Account(id, balance));

        AccountResponse res = service.getAccountById(id);

        assertThat(res.accountId()).isEqualTo(id);
        assertThat(res.balance()).isEqualByComparingTo(balance);
    }

    /** Missing account throws AccountNotFoundException. */
    @Test
    void get_AccountById_throws_whenAccountMissing() {
        UUID id = UUID.randomUUID();
        when(accountRepository.findById(id)).thenThrow(new AccountNotFoundException(id));

        assertThrows(AccountNotFoundException.class, () -> service.getAccountById(id));
    }
}
