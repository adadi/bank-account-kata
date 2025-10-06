package com.kata.bankaccount.adapter.persistence.jpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "operations")
public class OperationEntity {
    @Id
    private UUID id;

    private Instant createdAt = Instant.now();

    public OperationEntity() {}

    public OperationEntity(UUID id) { this.id = id; }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}

