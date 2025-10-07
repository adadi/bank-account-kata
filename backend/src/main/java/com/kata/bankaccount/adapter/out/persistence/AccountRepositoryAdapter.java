package com.kata.bankaccount.adapter.out.persistence;

import com.kata.bankaccount.adapter.out.persistence.jpa.entity.AccountEntity;
import com.kata.bankaccount.adapter.out.persistence.jpa.entity.TransactionEntity;
import com.kata.bankaccount.adapter.out.persistence.jpa.repository.AccountJpaRepository;
import com.kata.bankaccount.domain.exception.AccountNotFoundException;
import com.kata.bankaccount.application.ports.out.AccountRepository;
import com.kata.bankaccount.domain.model.Account;
import com.kata.bankaccount.domain.model.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Repository
@Transactional
@Slf4j
public class AccountRepositoryAdapter implements AccountRepository {

    private final AccountJpaRepository accountJpaRepository;

    public AccountRepositoryAdapter(AccountJpaRepository accountJpaRepository) {
        this.accountJpaRepository = accountJpaRepository;
    }

    @Override
    public Account lockById(UUID accountId) {
        var entity = accountJpaRepository.lockById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
        // Map to domain. Existing transactions are not copied to domain to keep domain minimal.
        return new Account(entity.getId(), entity.getBalance());
    }

    @Override
    public void save(Account account) {
        var entity = accountJpaRepository.findByIdWithTransactions(account.getId())
                .orElse(new AccountEntity(account.getId(), account.getBalance()));

        entity.setBalance(account.getBalance());

        // Append only new transactions by ID
        Set<UUID> existingIds = new HashSet<>();
        if (entity.getTransactions() != null) {
            for (var te : entity.getTransactions()) existingIds.add(te.getId());
        }

        for (Transaction t : account.getTransactions()) {
            if (!existingIds.contains(t.getId())) {
                var te = new TransactionEntity(
                        t.getId(),
                        entity,
                        t.getType(),
                        t.getAmount(),
                        t.getTimestamp(),
                        t.getResultingBalance()
                );
                entity.getTransactions().add(te);
            }
        }

        accountJpaRepository.save(entity);
    }

    @Override
    public Account findById(UUID accountId) {
        log.info("findById count",accountJpaRepository.count());
        var entity = accountJpaRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
        return new Account(entity.getId(), entity.getBalance());
    }
}
