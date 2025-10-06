package com.kata.bankaccount.adapter.persistence;

import com.kata.bankaccount.adapter.out.persistence.AccountRepositoryAdapter;
import com.kata.bankaccount.application.ports.out.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RepositoryWiringTest {

    @Autowired
    AccountRepository accountRepository;

    @Test
    void jpaAdapterImplementsAccountRepositoryPort() {
        assertThat(accountRepository).isNotNull();
        assertThat(accountRepository).isInstanceOf(AccountRepositoryAdapter.class);
    }
}

