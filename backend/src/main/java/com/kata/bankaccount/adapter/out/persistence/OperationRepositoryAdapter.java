package com.kata.bankaccount.adapter.out.persistence;

import com.kata.bankaccount.adapter.out.persistence.jpa.entity.OperationEntity;
import com.kata.bankaccount.adapter.out.persistence.jpa.repository.OperationJpaRepository;
import com.kata.bankaccount.application.ports.out.OperationRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
@Transactional
public class OperationRepositoryAdapter implements OperationRepository {
    private final OperationJpaRepository repo;

    public OperationRepositoryAdapter(OperationJpaRepository repo) {
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

