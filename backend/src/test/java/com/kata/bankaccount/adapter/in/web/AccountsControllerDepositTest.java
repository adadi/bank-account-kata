package com.kata.bankaccount.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kata.bankaccount.adapter.in.web.AccountsController;
import com.kata.bankaccount.application.ports.in.DepositUseCase;
import com.kata.bankaccount.application.ports.in.ListTransactionsUseCase;
import com.kata.bankaccount.application.ports.in.WithdrawUseCase;
import com.kata.bankaccount.application.ports.in.GetAccountUseCase;
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
class AccountsControllerDepositTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean DepositUseCase depositUseCase;
    @MockBean WithdrawUseCase withdrawUseCase;
    @MockBean ListTransactionsUseCase listTransactionsUseCase;
    @MockBean GetAccountUseCase getAccountUseCase;

    @Test
    void deposit_returns404_whenAccountNotFound() throws Exception {
        UUID accountId = UUID.randomUUID();
        UUID operationId = UUID.randomUUID();

        given(depositUseCase.deposit(eq(accountId), eq(new BigDecimal("10.00")), eq(operationId)))
                .willThrow(new AccountNotFoundException(accountId, operationId));

        var body = Map.of(
                "amount", "10.00",
                "operationId", operationId.toString()
        );

        mockMvc.perform(post("/accounts/" + accountId + "/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ACCOUNT_NOT_FOUND"));
    }
}
