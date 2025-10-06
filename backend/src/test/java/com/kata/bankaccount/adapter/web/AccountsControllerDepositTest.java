package com.kata.bankaccount.adapter.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kata.bankaccount.adapter.in.web.AccountsController;
import com.kata.bankaccount.application.dto.response.DepositResponse;
import com.kata.bankaccount.application.ports.in.DepositUseCase;
import com.kata.bankaccount.application.ports.in.WithdrawUseCase;
import com.kata.bankaccount.application.ports.in.ListTransactionsUseCase;
import com.kata.bankaccount.domain.model.Account;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AccountsController.class)
class AccountsControllerDepositTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean DepositUseCase depositUseCase;
    @MockBean WithdrawUseCase withdrawUseCase;
    @MockBean ListTransactionsUseCase listTransactionsUseCase;

    @Test
    void deposit_returns201_whenApplied() throws Exception {
        UUID accountId = UUID.randomUUID();
        UUID operationId = UUID.randomUUID();
        Account account = new Account(accountId, new BigDecimal("50"));
        given(depositUseCase.deposit(eq(accountId), eq(new BigDecimal("50.00")), eq(operationId)))
                .willReturn(new DepositResponse(account.getId(), account.getBalance(), true));

        var body = Map.of(
                "amount", "50.00",
                "operationId", operationId.toString()
        );

        mockMvc.perform(post("/accounts/" + accountId + "/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountId").value(accountId.toString()))
                .andExpect(jsonPath("$.balance").value(50.00));
    }

    @Test
    void deposit_returns200_whenIdempotent() throws Exception {
        UUID accountId = UUID.randomUUID();
        UUID operationId = UUID.randomUUID();
        Account account = new Account(accountId, new BigDecimal("50"));
        given(depositUseCase.deposit(eq(accountId), eq(new BigDecimal("50.00")), eq(operationId)))
                .willReturn(new DepositResponse(account.getId(), account.getBalance(), false));

        var body = Map.of(
                "amount", "50.00",
                "operationId", operationId.toString()
        );

        mockMvc.perform(post("/accounts/" + accountId + "/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(50.00));
    }

    @Test
    void deposit_returns400_whenAmountMissingOrInvalid() throws Exception {
        UUID accountId = UUID.randomUUID();
        UUID operationId = UUID.randomUUID();

        // Missing amount
        var bodyMissing = Map.of("operationId", operationId.toString());
        mockMvc.perform(post("/accounts/" + accountId + "/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bodyMissing)))
                .andExpect(status().isBadRequest());

        // amount <= 0
        var bodyInvalid = Map.of(
                "amount", "0",
                "operationId", operationId.toString()
        );
        // stub use case to avoid NullPointer (won't be called due to validation)
        given(depositUseCase.deposit(eq(accountId), any(), eq(operationId)))
                .willReturn(new DepositResponse(accountId, BigDecimal.ZERO, true));

        mockMvc.perform(post("/accounts/" + accountId + "/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bodyInvalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deposit_returns400_whenOperationIdMissing() throws Exception {
        UUID accountId = UUID.randomUUID();
        var body = Map.of("amount", "10.00");
        mockMvc.perform(post("/accounts/" + accountId + "/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }
}
