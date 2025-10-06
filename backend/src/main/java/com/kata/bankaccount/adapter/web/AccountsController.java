package com.kata.bankaccount.adapter.web;

import com.kata.bankaccount.application.dto.request.DepositRequest;
import com.kata.bankaccount.application.dto.request.WithdrawRequest;
import com.kata.bankaccount.application.dto.response.DepositResponse;
import com.kata.bankaccount.application.dto.response.WithdrawResponse;
import com.kata.bankaccount.application.ports.in.AccountUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/accounts")
public class AccountsController {

    private final AccountUseCase accountUseCase;

    public AccountsController(AccountUseCase accountUseCase) {
        this.accountUseCase = accountUseCase;
    }

    @PostMapping("/{id}/deposit")
    public ResponseEntity<DepositResponse> deposit(
            @PathVariable("id") UUID accountId,
            @Valid @RequestBody DepositRequest request
    ) {
        DepositResponse response = accountUseCase.deposit(accountId, request.amount(), request.operationId());
        HttpStatus status = response.applied() ? HttpStatus.CREATED : HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    @PostMapping("/{id}/withdraw")
    public ResponseEntity<WithdrawResponse> withdraw(
            @PathVariable("id") UUID accountId,
            @Valid @RequestBody WithdrawRequest request
    ) {
        var response = accountUseCase.withdraw(accountId, request.amount());
        return ResponseEntity.ok(response);
    }
}
