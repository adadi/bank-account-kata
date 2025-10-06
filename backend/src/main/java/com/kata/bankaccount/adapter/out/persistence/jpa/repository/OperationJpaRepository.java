package com.kata.bankaccount.adapter.out.persistence.jpa.repository;

import com.kata.bankaccount.adapter.out.persistence.jpa.entity.OperationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OperationJpaRepository extends JpaRepository<OperationEntity, UUID> {
}

