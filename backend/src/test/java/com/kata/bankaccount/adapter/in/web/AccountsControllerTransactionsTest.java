package com.kata.bankaccount.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kata.bankaccount.application.dto.response.TransactionResponse;
import com.kata.bankaccount.application.ports.in.ListTransactionsUseCase;
import com.kata.bankaccount.application.ports.in.DepositUseCase;
import com.kata.bankaccount.application.ports.in.WithdrawUseCase;
import com.kata.bankaccount.application.ports.in.GetAccountUseCase;
import com.kata.bankaccount.domain.exception.AccountNotFoundException;
import com.kata.bankaccount.domain.model.TransactionType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AccountsController.class)
class AccountsControllerTransactionsTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean ListTransactionsUseCase listTransactionsUseCase;
    @MockBean DepositUseCase depositUseCase;
    @MockBean WithdrawUseCase withdrawUseCase;
    @MockBean GetAccountUseCase getAccountUseCase;

    @Test
    void transactions_returnsList_sortedDesc_shapeOk() throws Exception {
        UUID accountId = UUID.randomUUID();
        var t1 = Instant.parse("2024-01-01T10:00:00Z");
        var t2 = Instant.parse("2024-01-01T11:00:00Z");
        var t3 = Instant.parse("2024-01-01T12:00:00Z");
        // Mocked use case returns already-sorted list (desc)
        given(listTransactionsUseCase.transactions(eq(accountId), any(), any())).willReturn(List.of(
                new TransactionResponse(TransactionType.DEPOSIT, new BigDecimal("10.00"), t3, new BigDecimal("80.00")),
                new TransactionResponse(TransactionType.WITHDRAWAL, new BigDecimal("30.00"), t2, new BigDecimal("70.00")),
                new TransactionResponse(TransactionType.DEPOSIT, new BigDecimal("100.00"), t1, new BigDecimal("100.00"))
        ));

        mockMvc.perform(get("/v1/accounts/" + accountId + "/transactions")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("DEPOSIT"))
                .andExpect(jsonPath("$[0].amount").value(10.00))
                .andExpect(jsonPath("$[0].resultingBalance").value(80.00))
                .andExpect(jsonPath("$[1].type").value("WITHDRAWAL"))
                .andExpect(jsonPath("$[2].amount").value(100.00));
    }

    @Test
    void transactions_parsesFromAndToParams_andForwardsToUseCase() throws Exception {
        UUID accountId = UUID.randomUUID();
        var from = "2024-01-01T00:00:00Z";
        var to = "2024-02-01T00:00:00Z";

        given(listTransactionsUseCase.transactions(eq(accountId), any(), any())).willReturn(List.of());

        mockMvc.perform(get("/v1/accounts/" + accountId + "/transactions")
                        .param("from", from)
                        .param("to", to)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(listTransactionsUseCase).transactions(eq(accountId), eq(Instant.parse(from)), eq(Instant.parse(to)));
    }

    @Test
    void transactions_returns404_whenAccountNotFound() throws Exception {
        UUID accountId = UUID.randomUUID();
        given(listTransactionsUseCase.transactions(eq(accountId), any(), any()))
                .willThrow(new AccountNotFoundException(accountId));

        mockMvc.perform(get("/v1/accounts/" + accountId + "/transactions")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ACCOUNT_NOT_FOUND"));
    }
}
