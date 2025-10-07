package com.kata.bankaccount.application.service;

import com.kata.bankaccount.application.dto.response.DepositResponse;
import com.kata.bankaccount.application.dto.response.AccountResponse;
import com.kata.bankaccount.application.dto.response.TransactionResponse;
import com.kata.bankaccount.application.dto.response.WithdrawResponse;
import com.kata.bankaccount.application.ports.in.DepositUseCase;
import com.kata.bankaccount.application.ports.in.GetAccountUseCase;
import com.kata.bankaccount.application.ports.in.ListTransactionsUseCase;
import com.kata.bankaccount.application.ports.in.WithdrawUseCase;
import com.kata.bankaccount.application.ports.out.AccountRepository;
import com.kata.bankaccount.application.ports.out.OperationRepository;
import com.kata.bankaccount.application.ports.out.TransactionyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class AccountService implements DepositUseCase, WithdrawUseCase, ListTransactionsUseCase, GetAccountUseCase {
    private final AccountRepository accountRepository;
    private final OperationRepository operationRepository;
    private final TransactionyRepository transactionyRepository;

    public AccountService(AccountRepository accountRepository,
                          OperationRepository operationRepository,
                          TransactionyRepository transactionyRepository) {
        this.accountRepository = accountRepository;
        this.operationRepository = operationRepository;
        this.transactionyRepository = transactionyRepository;
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

    @Override
    @Transactional
    public List<TransactionResponse> transactions(UUID accountId, Instant from, Instant to) {
        Objects.requireNonNull(accountId, "accountId");
        // Ensure account exists → 404 when missing
        accountRepository.lockById(accountId);
        return transactionyRepository.findByAccountAndPeriod(accountId, from, to)
                .stream()
                .map(t -> new TransactionResponse(
                        t.getType(),
                        t.getAmount(),
                        t.getTimestamp(),
                        t.getResultingBalance()
                ))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponse get(UUID accountId) {
        Objects.requireNonNull(accountId, "accountId");
        var account = accountRepository.findById(accountId);
        return new AccountResponse(account.getId(), account.getBalance());
    }
}
