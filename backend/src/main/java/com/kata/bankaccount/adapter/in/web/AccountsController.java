package com.kata.bankaccount.adapter.in.web;

import com.kata.bankaccount.application.dto.request.DepositRequest;
import com.kata.bankaccount.application.dto.request.WithdrawRequest;
import com.kata.bankaccount.application.dto.response.DepositResponse;
import com.kata.bankaccount.application.dto.response.WithdrawResponse;
import com.kata.bankaccount.application.dto.response.TransactionResponse;
import com.kata.bankaccount.application.ports.in.DepositUseCase;
import com.kata.bankaccount.application.ports.in.ListTransactionsUseCase;
import com.kata.bankaccount.application.ports.in.WithdrawUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
public class AccountsController {

    private final DepositUseCase depositUseCase;
    private final WithdrawUseCase withdrawUseCase;
    private final ListTransactionsUseCase listTransactionsUseCase;

    public AccountsController(DepositUseCase depositUseCase, WithdrawUseCase withdrawUseCase, ListTransactionsUseCase listTransactionsUseCase) {
        this.depositUseCase = depositUseCase;
        this.withdrawUseCase = withdrawUseCase;
        this.listTransactionsUseCase = listTransactionsUseCase;
    }

    @PostMapping("/{id}/deposit")
    public ResponseEntity<DepositResponse> deposit(
            @PathVariable("id") UUID accountId,
            @Valid @RequestBody DepositRequest request
    ) {
        DepositResponse response = depositUseCase.deposit(accountId, request.amount(), request.operationId());
        HttpStatus status = response.applied() ? HttpStatus.CREATED : HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    @PostMapping("/{id}/withdraw")
    public ResponseEntity<WithdrawResponse> withdraw(
            @PathVariable("id") UUID accountId,
            @Valid @RequestBody WithdrawRequest request
    ) {
        var response = withdrawUseCase.withdraw(accountId, request.amount(), request.operationId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/transactions")
    public List<TransactionResponse> transactions(
            @PathVariable("id") UUID accountId,
            @RequestParam(value = "from", required = false) Instant from,
            @RequestParam(value = "to", required = false) Instant to
    ) {
        return listTransactionsUseCase.transactions(accountId, from, to);
    }
}
