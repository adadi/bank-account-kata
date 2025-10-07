package com.kata.bankaccount.adapter.in.web;

import com.kata.bankaccount.application.dto.response.AccountResponse;
import com.kata.bankaccount.application.ports.in.DepositUseCase;
import com.kata.bankaccount.application.ports.in.GetAccountUseCase;
import com.kata.bankaccount.application.ports.in.ListTransactionsUseCase;
import com.kata.bankaccount.application.ports.in.WithdrawUseCase;
import com.kata.bankaccount.domain.exception.AccountNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AccountsController.class)
class AccountsControllerGetTest {

    @Autowired
    MockMvc mvc;

    @MockBean DepositUseCase depositUseCase;
    @MockBean WithdrawUseCase withdrawUseCase;
    @MockBean ListTransactionsUseCase listTransactionsUseCase;
    @MockBean GetAccountUseCase getAccountUseCase;

    @Test
    void getAccount_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        when(getAccountUseCase.get(id)).thenReturn(new AccountResponse(id, new BigDecimal("0.00")));

        mvc.perform(get("/accounts/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accountId").value(id.toString()))
                .andExpect(jsonPath("$.balance").exists());
    }

    @Test
    void getAccount_returns404_whenMissing() throws Exception {
        UUID id = UUID.randomUUID();
        when(getAccountUseCase.get(id)).thenThrow(new AccountNotFoundException(id));

        mvc.perform(get("/accounts/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("ACCOUNT_NOT_FOUND"));
    }
}

