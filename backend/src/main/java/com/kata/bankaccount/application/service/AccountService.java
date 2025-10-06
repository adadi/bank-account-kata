package com.kata.bankaccount.application.service;

import com.kata.bankaccount.application.dto.response.DepositResponse;
import com.kata.bankaccount.application.dto.response.WithdrawResponse;
import com.kata.bankaccount.application.ports.in.AccountUseCase;
import com.kata.bankaccount.application.ports.out.AccountRepository;
import com.kata.bankaccount.application.ports.out.OperationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public WithdrawResponse withdraw(UUID accountId, BigDecimal amount, UUID operationId) {
        Objects.requireNonNull(accountId, "accountId");
        Objects.requireNonNull(amount, "amount");
        Objects.requireNonNull(operationId, "operationId");

        var account = accountRepository.lockById(accountId);
        if (operationRepository.exists(operationId)) {
            return new WithdrawResponse(account.getId(), account.getBalance());
        }

        account.withdraw(amount);
        accountRepository.save(account);
        operationRepository.save(operationId);
        return new WithdrawResponse(account.getId(), account.getBalance());
    }

    @Override
    @Transactional
    public DepositResponse deposit(UUID accountId, BigDecimal amount, UUID operationId) {
        Objects.requireNonNull(accountId, "accountId");
        Objects.requireNonNull(operationId, "operationId");

        var account = accountRepository.lockById(accountId);
        if (operationRepository.exists(operationId)) {
            // No-op, return current state
            return new DepositResponse(account.getId(), account.getBalance(), false);
        }

        account.deposit(amount); // domain validates amount > 0
        accountRepository.save(account);
        operationRepository.save(operationId);
        return new DepositResponse(account.getId(), account.getBalance(), true);
    }
}
