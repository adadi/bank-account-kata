package com.kata.bankaccount.adapter.persistence;

import com.kata.bankaccount.adapter.persistence.jpa.entity.OperationEntity;
import com.kata.bankaccount.adapter.persistence.jpa.repository.OperationJpaRepository;
import com.kata.bankaccount.application.ports.out.IdempotencyRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
@Transactional
public class JpaIdempotencyRepositoryAdapter implements IdempotencyRepository {
    private final OperationJpaRepository repo;

    public JpaIdempotencyRepositoryAdapter(OperationJpaRepository repo) {
        this.repo = repo;
    }

    @Override
    public boolean exists(UUID operationId) {
        return repo.existsById(operationId);
    }

    @Override
    public void save(UUID operationId) {
        if (!repo.existsById(operationId)) {
            repo.save(new OperationEntity(operationId));
        }
    }
}

