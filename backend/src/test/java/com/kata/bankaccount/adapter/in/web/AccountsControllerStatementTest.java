package com.kata.bankaccount.adapter.in.web;

import com.kata.bankaccount.application.ports.in.DepositUseCase;
import com.kata.bankaccount.application.ports.in.GetAccountUseCase;
import com.kata.bankaccount.application.ports.in.ListTransactionsUseCase;
import com.kata.bankaccount.application.ports.in.WithdrawUseCase;
import com.kata.bankaccount.application.ports.in.ExportStatementUseCase;
import com.kata.bankaccount.domain.exception.AccountNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AccountsController.class)
class AccountsControllerStatementTest {

    @Autowired MockMvc mockMvc;

    @MockBean ListTransactionsUseCase listTransactionsUseCase;
    @MockBean ExportStatementUseCase exportStatementUseCase;
    @MockBean DepositUseCase depositUseCase;
    @MockBean WithdrawUseCase withdrawUseCase;
    @MockBean GetAccountUseCase getAccountUseCase;

    @Test
    void statement_returnsCsv_withHeader_andRows() throws Exception {
        UUID accountId = UUID.randomUUID();
        var expected = String.join("\n",
                "date,operation,amount,balanceAfter",
                "2024-01-01T12:00:00Z,DEPOSIT,10.00,80.00",
                "2024-01-01T11:00:00Z,WITHDRAWAL,30.00,70.00",
                "2024-01-01T10:00:00Z,DEPOSIT,100.00,100.00",
                "");

        given(exportStatementUseCase.statementCsv(eq(accountId), any(), any())).willReturn(expected);

        mockMvc.perform(get("/v1/accounts/" + accountId + "/statement")
                        .accept(MediaType.valueOf("text/csv")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/csv"))
                .andExpect(content().string(expected));
    }

    @Test
    void statement_parsesFromAndToParams_andForwardsToUseCase() throws Exception {
        UUID accountId = UUID.randomUUID();
        var from = "2024-01-01";
        var to = "2024-02-01";

        given(exportStatementUseCase.statementCsv(eq(accountId), any(), any())).willReturn("date,operation,amount,balanceAfter\n");

        mockMvc.perform(get("/v1/accounts/" + accountId + "/statement")
                        .param("from", from)
                        .param("to", to)
                        .accept(MediaType.valueOf("text/csv")))
                .andExpect(status().isOk());

        verify(exportStatementUseCase).statementCsv(eq(accountId), eq(LocalDate.parse(from)), eq(LocalDate.parse(to)));
    }

    @Test
    void statement_returns404_whenAccountNotFound() throws Exception {
        UUID accountId = UUID.randomUUID();
        given(exportStatementUseCase.statementCsv(eq(accountId), any(), any()))
                .willThrow(new AccountNotFoundException(accountId));

        mockMvc.perform(get("/v1/accounts/" + accountId + "/statement")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ACCOUNT_NOT_FOUND"));
    }
}
