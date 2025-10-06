package com.kata.bankaccount.adapter.out.persistence.jpa.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "operations")
public class OperationEntity {
    @Id
    private UUID id;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public OperationEntity() {}

    public OperationEntity(UUID id) { this.id = id; }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
