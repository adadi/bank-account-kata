package com.kata.bankaccount;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kata.bankaccount.adapter.out.persistence.jpa.entity.AccountEntity;
import com.kata.bankaccount.adapter.out.persistence.jpa.repository.AccountJpaRepository;
import com.kata.bankaccount.domain.model.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the deposit HTTP endpoint using MockMvc and a real
 * persistence layer. Verifies idempotency, 404 handling and DB effects.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class DepositIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired AccountJpaRepository accountJpaRepository;

    UUID accountId;

    /** Prepares a fresh account with zero balance for each test. */
    @BeforeEach
    void setup() {
        accountId = UUID.randomUUID();
        accountJpaRepository.save(new AccountEntity(accountId, BigDecimal.ZERO));
    }

    /**
     * Depositing 50 increases balance and creates one DEPOSIT transaction.
     */
    @Test
    void deposit_increasesBalance_andAddsTransactionRow() throws Exception {
        // Given
        var operationId = UUID.randomUUID();
        var body = Map.of(
                "amount", "50.00",
                "operationId", operationId.toString()
        );

        // When
        mockMvc.perform(post("/v1/accounts/" + accountId + "/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated());

        // Then
        var accountEntity = accountJpaRepository.findByIdWithTransactions(accountId).orElseThrow();
        assertThat(accountEntity.getBalance()).isEqualByComparingTo("50.00");
        assertThat(accountEntity.getTransactions()).hasSize(1);
        assertThat(accountEntity.getTransactions().get(0).getType()).isEqualTo(TransactionType.DEPOSIT);
    }

    /**
     * Two identical requests with the same operationId result in a single applied deposit.
     */
    @Test
    void deposit_isIdempotent_forSameOperationId() throws Exception {
        // Given
        var operationId = UUID.randomUUID();
        var body = Map.of(
                "amount", "50.00",
                "operationId", operationId.toString()
        );

        // When - first call applied
        mockMvc.perform(post("/v1/accounts/" + accountId + "/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated());

        // When - second call idempotent
        mockMvc.perform(post("/v1/accounts/" + accountId + "/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk());

        // Then
        var accountEntity = accountJpaRepository.findByIdWithTransactions(accountId).orElseThrow();
        assertThat(accountEntity.getBalance()).isEqualByComparingTo("50.00");
        assertThat(accountEntity.getTransactions()).hasSize(1);
        assertThat(accountEntity.getTransactions().get(0).getType()).isEqualTo(TransactionType.DEPOSIT);
    }

    /**
     * Depositing to a missing account returns 404 with error code.
     */
    @Test
    void deposit_nonExistentAccount_returns404_withCode() throws Exception {
        UUID missingAccountId = UUID.randomUUID();
        UUID operationId = UUID.randomUUID();
        var body = Map.of(
                "amount", "10.00",
                "operationId", operationId.toString()
        );

        mockMvc.perform(post("/v1/accounts/" + missingAccountId + "/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ACCOUNT_NOT_FOUND"));
    }
}
