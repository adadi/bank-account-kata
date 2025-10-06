package com.kata.bankaccount.application.service;

import com.kata.bankaccount.application.ports.in.AccountUseCase;
import com.kata.bankaccount.application.ports.out.AccountRepository;
import com.kata.bankaccount.domain.model.Account;

import java.math.BigDecimal;
import java.util.UUID;

public class AccountService implements AccountUseCase {
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Account withdraw(UUID accountId, BigDecimal amount) {
        var account = accountRepository.lockById(accountId);
        account.withdraw(amount);
        accountRepository.save(account);
        return account;
    }
}

