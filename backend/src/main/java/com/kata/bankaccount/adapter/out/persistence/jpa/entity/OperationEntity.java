package com.kata.bankaccount.adapter.out.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

/**
 * JPA entity used to persist idempotent operation ids.
 */
@Setter
@Getter
@Entity
@Table(name = "operations")
public class OperationEntity {
    /**
     * The operation ID.
     */
    @Id
    private UUID id;
    /**
     * The timestamp when the operation was created.
     */
    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    /**
     * Default constructor for JPA.
     */
    public OperationEntity() {
    }

    /**
     * Creates the entity with an identifier.
     *
     * @param id the operation ID
     */
    public OperationEntity(UUID id) {
        this.id = id;
    }

}
