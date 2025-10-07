package com.kata.bankaccount.application.service;

import com.kata.bankaccount.application.dto.response.AccountResponse;
import com.kata.bankaccount.application.ports.out.AccountRepository;
import com.kata.bankaccount.application.ports.out.OperationRepository;
import com.kata.bankaccount.application.ports.out.TransactionyRepository;
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

class AccountServiceGetTest {

    private AccountRepository accountRepository;
    private OperationRepository operationRepository;
    private TransactionyRepository transactionyRepository;
    private AccountService service;

    @BeforeEach
    void setUp() {
        accountRepository = Mockito.mock(AccountRepository.class);
        operationRepository = Mockito.mock(OperationRepository.class);
        transactionyRepository = Mockito.mock(TransactionyRepository.class);
        service = new AccountService(accountRepository, operationRepository, transactionyRepository);
    }

    @Test
    void get_returnsAccountResponse_whenAccountExists() {
        UUID id = UUID.randomUUID();
        BigDecimal balance = new BigDecimal("123.45");
        when(accountRepository.findById(id)).thenReturn(new Account(id, balance));

        AccountResponse res = service.get(id);

        assertThat(res.accountId()).isEqualTo(id);
        assertThat(res.balance()).isEqualByComparingTo(balance);
    }

    @Test
    void get_throws_whenAccountMissing() {
        UUID id = UUID.randomUUID();
        when(accountRepository.findById(id)).thenThrow(new AccountNotFoundException(id));

        assertThrows(AccountNotFoundException.class, () -> service.get(id));
    }
}

