package com.kata.bankaccount.application.service;

import com.kata.bankaccount.application.dto.response.DepositResponse;
import com.kata.bankaccount.application.dto.response.WithdrawResponse;
import com.kata.bankaccount.application.ports.in.AccountUseCase;
import com.kata.bankaccount.application.ports.out.AccountRepository;
import com.kata.bankaccount.application.ports.out.OperationRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Service
public class AccountService implements AccountUseCase {
    private final AccountRepository accountRepository;
    private final OperationRepository operationRepository;

    public AccountService(AccountRepository accountRepository, OperationRepository operationRepository) {
        this.accountRepository = accountRepository;
        this.operationRepository = operationRepository;
    }

    @Override
    public WithdrawResponse withdraw(UUID accountId, BigDecimal amount) {
        Objects.requireNonNull(accountId, "accountId");
        Objects.requireNonNull(amount, "amount");

        var account = accountRepository.lockById(accountId);
        account.withdraw(amount);
        accountRepository.save(account);
        return new WithdrawResponse(account.getId(), account.getBalance());
    }

    @Override
    public DepositResponse deposit(UUID accountId, BigDecimal amount, UUID operationId) {
        Objects.requireNonNull(accountId, "accountId");
        Objects.requireNonNull(operationId, "operationId");

        if (operationRepository.exists(operationId)) {
            // No-op, return current state (locked for consistency)
            var current = accountRepository.lockById(accountId);
            return new DepositResponse(current.getId(), current.getBalance(), false);
        }

        var account = accountRepository.lockById(accountId);
        account.deposit(amount); // domain validates amount > 0
        accountRepository.save(account);
        operationRepository.save(operationId);
        return new DepositResponse(account.getId(), account.getBalance(), true);
    }
}
