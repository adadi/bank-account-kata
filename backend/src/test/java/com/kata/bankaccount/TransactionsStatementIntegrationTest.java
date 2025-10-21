package com.kata.bankaccount;

import com.fasterxml.jackson.databind.JsonNode;
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
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for listing transactions and CSV statement export
 * ensuring order, field correctness and period filtering.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class TransactionsStatementIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired AccountJpaRepository accountJpaRepository;

    UUID accountId;

    /** Creates a new account with zero balance for each test. */
    @BeforeEach
    void setup() {
        accountId = UUID.randomUUID();
        accountJpaRepository.save(new AccountEntity(accountId, BigDecimal.ZERO));
    }

    /**
     * After D(100), W(30), D(10), the transactions endpoint returns 3 rows sorted desc
     * with expected resulting balances and period filtering works from a given timestamp.
     */
    @Test
    void statement_returnsRows_sortedDesc_andFieldsAreCorrect() throws Exception {
        // Given: 3 operations Deposit 100, Withdraw 30, Deposit 10
        var op1 = UUID.randomUUID();
        var op2 = UUID.randomUUID();
        var op3 = UUID.randomUUID();

        callDeposit(accountId, "100.00", op1)
                .andExpect(status().isCreated());
        callWithdraw(accountId, "30.00", op2)
                .andExpect(status().isOk());
        callDeposit(accountId, "10.00", op3)
                .andExpect(status().isCreated());

        // When: I request the statement
        var mvcResult = mockMvc.perform(get("/v1/accounts/" + accountId + "/transactions")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode arr = objectMapper.readTree(mvcResult.getResponse().getContentAsString());

        // Then: I get these 3 rows in order with correct post-balances
        assertThat(arr).hasSize(3);
        assertThat(arr.get(0).get("type").asText()).isEqualTo(TransactionType.DEPOSIT.name());
        assertThat(arr.get(0).get("amount").asDouble()).isEqualTo(10.00);
        assertThat(arr.get(0).get("resultingBalance").asDouble()).isEqualTo(80.00);

        assertThat(arr.get(1).get("type").asText()).isEqualTo(TransactionType.WITHDRAWAL.name());
        assertThat(arr.get(1).get("amount").asDouble()).isEqualTo(30.00);
        assertThat(arr.get(1).get("resultingBalance").asDouble()).isEqualTo(70.00);

        assertThat(arr.get(2).get("type").asText()).isEqualTo(TransactionType.DEPOSIT.name());
        assertThat(arr.get(2).get("amount").asDouble()).isEqualTo(100.00);
        assertThat(arr.get(2).get("resultingBalance").asDouble()).isEqualTo(100.00);

        // And: Filtering by period (from = timestamp of withdrawal) returns two last rows
        // Fetch timestamp of the withdrawal from DB
        var accountEntity = accountJpaRepository.findByIdWithTransactions(accountId).orElseThrow();
        var withdrawalTs = accountEntity.getTransactions().stream()
                .filter(t -> t.getType() == TransactionType.WITHDRAWAL)
                .findFirst().orElseThrow()
                .getTimestamp();

        var mvcFiltered = mockMvc.perform(get("/v1/accounts/" + accountId + "/transactions")
                        .param("from", withdrawalTs.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode arrFiltered = objectMapper.readTree(mvcFiltered.getResponse().getContentAsString());
        assertThat(arrFiltered).hasSize(2);
        assertThat(Instant.parse(arrFiltered.get(0).get("timestamp").asText()))
                .isAfterOrEqualTo(withdrawalTs);
        assertThat(Instant.parse(arrFiltered.get(1).get("timestamp").asText()))
                .isAfterOrEqualTo(withdrawalTs);
    }

    /** Missing account returns 404 and error code. */
    @Test
    void transactions_nonExistentAccount_returns404_withCode() throws Exception {
        UUID missingAccountId = UUID.randomUUID();

        mockMvc.perform(get("/v1/accounts/" + missingAccountId + "/transactions")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ACCOUNT_NOT_FOUND"));
    }

    /** Helper to call the deposit endpoint. */
    private org.springframework.test.web.servlet.ResultActions callDeposit(UUID accountId, String amount, UUID operationId) throws Exception {
        var body = Map.of(
                "amount", amount,
                "operationId", operationId.toString()
        );
        return mockMvc.perform(post("/v1/accounts/" + accountId + "/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)));
    }

    /** Helper to call the withdraw endpoint. */
    private org.springframework.test.web.servlet.ResultActions callWithdraw(UUID accountId, String amount, UUID operationId) throws Exception {
        var body = Map.of(
                "amount", amount,
                "operationId", operationId.toString()
        );
        return mockMvc.perform(post("/v1/accounts/" + accountId + "/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)));
    }
}
