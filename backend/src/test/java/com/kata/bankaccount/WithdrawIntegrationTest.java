package com.kata.bankaccount;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kata.bankaccount.adapter.out.persistence.jpa.entity.AccountEntity;
import com.kata.bankaccount.adapter.out.persistence.jpa.repository.AccountJpaRepository;
import com.kata.bankaccount.domain.model.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class WithdrawIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired AccountJpaRepository accountJpaRepository;

    UUID accountId;

    @BeforeEach
    void setup() {
        accountId = UUID.randomUUID();
        accountJpaRepository.save(new AccountEntity(accountId, new BigDecimal("100.00")));
    }

    @Test
    void withdraw_isIdempotent_forSameOperationId() throws Exception {
        // Given
        var operationId = UUID.randomUUID();
        var body = Map.of(
                "amount", "40.00",
                "operationId", operationId.toString()
        );

        // When - first call applied
        mockMvc.perform(post("/accounts/" + accountId + "/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk());

        // When - second call idempotent
        mockMvc.perform(post("/accounts/" + accountId + "/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk());

        // Then
        var accountEntity = accountJpaRepository.findByIdWithTransactions(accountId).orElseThrow();
        assertThat(accountEntity.getBalance()).isEqualByComparingTo("60.00");
        assertThat(accountEntity.getTransactions()).hasSize(1);
        assertThat(accountEntity.getTransactions().get(0).getType()).isEqualTo(TransactionType.WITHDRAWAL);
    }

    @Test
    @Timeout(10)
    void concurrent_withdrawals_onlyOneSucceeds_andBalanceIs20() throws Exception {
        // Given
        var op1 = UUID.randomUUID();
        var op2 = UUID.randomUUID();
        var amount = "80.00";

        // Latches to synchronize start of both requests
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);

        ExecutorService pool = Executors.newFixedThreadPool(2);
        try {
            Callable<Integer> task1 = () -> doWithdrawConcurrent(op1, amount, ready, start);
            Callable<Integer> task2 = () -> doWithdrawConcurrent(op2, amount, ready, start);

            Future<Integer> f1 = pool.submit(task1);
            Future<Integer> f2 = pool.submit(task2);

            // Wait until both threads are ready
            ready.await();
            // Fire both at the same time
            start.countDown();

            int s1 = f1.get();
            int s2 = f2.get();

            // Then: one OK, one CONFLICT
            var statuses = new ArrayList<Integer>();
            statuses.add(s1);
            statuses.add(s2);
            assertThat(statuses).containsExactlyInAnyOrder(200, 409);

            // And: final state is balance 20 with a single withdrawal transaction
            var accountEntity = accountJpaRepository.findByIdWithTransactions(accountId).orElseThrow();
            assertThat(accountEntity.getBalance()).isEqualByComparingTo("20.00");
            assertThat(accountEntity.getTransactions()).hasSize(1);
            assertThat(accountEntity.getTransactions().get(0).getType()).isEqualTo(TransactionType.WITHDRAWAL);
        } finally {
            pool.shutdownNow();
        }
    }

    private int doWithdrawConcurrent(UUID operationId, String amount, CountDownLatch ready, CountDownLatch start) throws Exception {
        ready.countDown();
        start.await();
        var body = Map.of(
                "amount", amount,
                "operationId", operationId.toString()
        );
        return mockMvc.perform(post("/accounts/" + accountId + "/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andReturn()
                .getResponse()
                .getStatus();
    }
}
