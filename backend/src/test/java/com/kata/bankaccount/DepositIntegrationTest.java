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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class DepositIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired AccountJpaRepository accountJpaRepository;

    UUID accountId;

    @BeforeEach
    void setup() {
        accountId = UUID.randomUUID();
        accountJpaRepository.save(new AccountEntity(accountId, BigDecimal.ZERO));
    }

    @Test
    void deposit_increasesBalance_andAddsTransactionRow() throws Exception {
        // Given
        var operationId = UUID.randomUUID();
        var body = Map.of(
                "amount", "50.00",
                "operationId", operationId.toString()
        );

        // When
        mockMvc.perform(post("/accounts/" + accountId + "/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated());

        // Then
        var accountEntity = accountJpaRepository.findByIdWithTransactions(accountId).orElseThrow();
        assertThat(accountEntity.getBalance()).isEqualByComparingTo("50.00");
        assertThat(accountEntity.getTransactions()).hasSize(1);
        assertThat(accountEntity.getTransactions().get(0).getType()).isEqualTo(TransactionType.DEPOSIT);
    }

    @Test
    void deposit_isIdempotent_forSameOperationId() throws Exception {
        // Given
        var operationId = UUID.randomUUID();
        var body = Map.of(
                "amount", "50.00",
                "operationId", operationId.toString()
        );

        // When - first call applied
        mockMvc.perform(post("/accounts/" + accountId + "/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated());

        // When - second call idempotent
        mockMvc.perform(post("/accounts/" + accountId + "/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk());

        // Then
        var accountEntity = accountJpaRepository.findByIdWithTransactions(accountId).orElseThrow();
        assertThat(accountEntity.getBalance()).isEqualByComparingTo("50.00");
        assertThat(accountEntity.getTransactions()).hasSize(1);
        assertThat(accountEntity.getTransactions().get(0).getType()).isEqualTo(TransactionType.DEPOSIT);
    }
}
