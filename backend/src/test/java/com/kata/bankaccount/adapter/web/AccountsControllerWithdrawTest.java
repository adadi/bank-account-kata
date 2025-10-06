package com.kata.bankaccount.adapter.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kata.bankaccount.adapter.in.web.AccountsController;
import com.kata.bankaccount.application.ports.in.WithdrawUseCase;
import com.kata.bankaccount.application.ports.in.DepositUseCase;
import com.kata.bankaccount.application.ports.in.ListTransactionsUseCase;
import com.kata.bankaccount.domain.exception.InsufficientFundsException;
import com.kata.bankaccount.domain.exception.AccountNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AccountsController.class)
class AccountsControllerWithdrawTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean WithdrawUseCase withdrawUseCase;
    @MockBean DepositUseCase depositUseCase;
    @MockBean ListTransactionsUseCase listTransactionsUseCase;

    @Test
    void withdraw_returns200_whenOk() throws Exception {
        UUID accountId = UUID.randomUUID();
        UUID operationId = UUID.randomUUID();

        given(withdrawUseCase.withdraw(eq(accountId), eq(new BigDecimal("40.00")), eq(operationId)))
                .willReturn(new com.kata.bankaccount.application.dto.response.WithdrawResponse(accountId, new BigDecimal("60.00")));

        var body = Map.of(
                "amount", "40.00",
                "operationId", operationId.toString()
        );

        mockMvc.perform(post("/accounts/" + accountId + "/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(accountId.toString()))
                .andExpect(jsonPath("$.balance").value(60.00));
    }

    @Test
    void withdraw_returns409_whenInsufficientFunds() throws Exception {
        UUID accountId = UUID.randomUUID();
        UUID operationId = UUID.randomUUID();

        given(withdrawUseCase.withdraw(eq(accountId), eq(new BigDecimal("120.00")), eq(operationId)))
                .willThrow(new InsufficientFundsException("Insufficient funds"));

        var body = Map.of(
                "amount", "120.00",
                "operationId", operationId.toString()
        );

        mockMvc.perform(post("/accounts/" + accountId + "/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("INSUFFICIENT_FUNDS"));
    }

    @Test
    void withdraw_returns400_whenInvalidAmount() throws Exception {
        UUID accountId = UUID.randomUUID();
        var body = Map.of(
                "amount", "0",
                "operationId", UUID.randomUUID().toString()
        );

        mockMvc.perform(post("/accounts/" + accountId + "/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void withdraw_returns404_withErrorJson_whenAccountNotFound() throws Exception {
        UUID accountId = UUID.randomUUID();
        UUID operationId = UUID.randomUUID();

        given(withdrawUseCase.withdraw(eq(accountId), eq(new BigDecimal("10.00")), eq(operationId)))
                .willThrow(new AccountNotFoundException(accountId, operationId));

        var body = Map.of(
                "amount", "10.00",
                "operationId", operationId.toString()
        );

        mockMvc.perform(post("/accounts/" + accountId + "/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ACCOUNT_NOT_FOUND"));
    }
}
