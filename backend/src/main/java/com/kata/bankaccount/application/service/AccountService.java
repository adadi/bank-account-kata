package com.kata.bankaccount.application.service;

import com.kata.bankaccount.application.ports.in.AccountUseCase;
import com.kata.bankaccount.application.dto.response.DepositResponse;
import com.kata.bankaccount.application.ports.out.AccountRepository;
import com.kata.bankaccount.application.ports.out.IdempotencyRepository;
import com.kata.bankaccount.domain.model.Account;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Service
public class AccountService implements AccountUseCase {
    private final AccountRepository accountRepository;
    private final IdempotencyRepository idempotencyRepository;

    public AccountService(AccountRepository accountRepository, IdempotencyRepository idempotencyRepository) {
        this.accountRepository = accountRepository;
        this.idempotencyRepository = idempotencyRepository;
    }

    @Override
    public Account withdraw(UUID accountId, BigDecimal amount) {
        var account = accountRepository.lockById(accountId);
        account.withdraw(amount);
        accountRepository.save(account);
        return account;
    }

    @Override
    public DepositResponse deposit(UUID accountId, BigDecimal amount, UUID operationId) {
        Objects.requireNonNull(accountId, "accountId");
        Objects.requireNonNull(operationId, "operationId");

        if (idempotencyRepository.exists(operationId)) {
            // No-op, return current state (locked for consistency)
            var current = accountRepository.lockById(accountId);
            return new DepositResponse(current.getId(), current.getBalance(), false);
        }

        var account = accountRepository.lockById(accountId);
        account.deposit(amount); // domain validates amount > 0
        accountRepository.save(account);
        idempotencyRepository.save(operationId);
        return new DepositResponse(account.getId(), account.getBalance(), true);
    }
}
