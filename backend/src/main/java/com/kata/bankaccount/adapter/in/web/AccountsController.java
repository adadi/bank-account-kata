package com.kata.bankaccount.adapter.in.web;

import com.kata.bankaccount.application.dto.request.DepositRequest;
import com.kata.bankaccount.application.dto.request.WithdrawRequest;
import com.kata.bankaccount.application.dto.response.DepositResponse;
import com.kata.bankaccount.application.dto.response.AccountResponse;
import com.kata.bankaccount.application.dto.response.WithdrawResponse;
import com.kata.bankaccount.application.dto.response.TransactionResponse;
import com.kata.bankaccount.application.ports.in.DepositUseCase;
import com.kata.bankaccount.application.ports.in.GetAccountUseCase;
import com.kata.bankaccount.application.ports.in.ListTransactionsUseCase;
import com.kata.bankaccount.application.ports.in.ExportStatementUseCase;
import com.kata.bankaccount.application.ports.in.WithdrawUseCase;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/accounts")
@Tag(name = "Accounts", description = "Operations on bank accounts")
public class AccountsController {

    private final DepositUseCase depositUseCase;
    private final WithdrawUseCase withdrawUseCase;
    private final ListTransactionsUseCase listTransactionsUseCase;
    private final GetAccountUseCase getAccountUseCase;
    private final ExportStatementUseCase exportStatementUseCase;

    public AccountsController(DepositUseCase depositUseCase, WithdrawUseCase withdrawUseCase, ListTransactionsUseCase listTransactionsUseCase, GetAccountUseCase getAccountUseCase, ExportStatementUseCase exportStatementUseCase) {
        this.depositUseCase = depositUseCase;
        this.withdrawUseCase = withdrawUseCase;
        this.listTransactionsUseCase = listTransactionsUseCase;
        this.getAccountUseCase = getAccountUseCase;
        this.exportStatementUseCase = exportStatementUseCase;
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get account",
            description = "Get account details (id and balance)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Account details",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccountResponse.class),
                            examples = @ExampleObject(value = "{\n  \"accountId\": \"6c0e3b06-8d1e-4b5f-8f9a-8d2d7a1a0c00\",\n  \"balance\": 150.00\n}"))),
            @ApiResponse(responseCode = "404", description = "Account not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.kata.bankaccount.adapter.in.web.dto.ApiErrorResponse.class),
                            examples = @ExampleObject(value = "{\n  \"code\": \"ACCOUNT_NOT_FOUND\",\n  \"message\": \"Account not found\"\n}")))
    })
    public AccountResponse getAccount(
            @Parameter(description = "Account ID") @PathVariable("id") UUID accountId
    ) {
        return getAccountUseCase.get(accountId);
    }

    @PostMapping("/{id}/deposit")
    @Operation(
            summary = "Deposit money",
            description = "Deposit a positive amount into the given account"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Deposit applied",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DepositResponse.class),
                            examples = @ExampleObject(name = "created",
                                    value = "{\n  \"accountId\": \"6c0e3b06-8d1e-4b5f-8f9a-8d2d7a1a0c00\",\n  \"balance\": 150.00,\n  \"applied\": true\n}"))) ,
            @ApiResponse(responseCode = "200", description = "Deposit already applied (idempotent)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DepositResponse.class),
                            examples = @ExampleObject(name = "ok",
                                    value = "{\n  \"accountId\": \"6c0e3b06-8d1e-4b5f-8f9a-8d2d7a1a0c00\",\n  \"balance\": 150.00,\n  \"applied\": false\n}"))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.kata.bankaccount.adapter.in.web.dto.ApiErrorResponse.class),
                            examples = @ExampleObject(value = "{\n  \"code\": \"VALIDATION_ERROR\",\n  \"message\": \"amount must be > 0\"\n}"))) ,
            @ApiResponse(responseCode = "404", description = "Account not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.kata.bankaccount.adapter.in.web.dto.ApiErrorResponse.class),
                            examples = @ExampleObject(value = "{\n  \"code\": \"ACCOUNT_NOT_FOUND\",\n  \"message\": \"Account not found\",\n  \"operationId\": \"11111111-1111-1111-1111-111111111111\"\n}")))
    })
    public ResponseEntity<DepositResponse> deposit(
            @Parameter(description = "Account ID") @PathVariable("id") UUID accountId,
            @Valid @org.springframework.web.bind.annotation.RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Deposit request",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DepositRequest.class),
                            examples = @ExampleObject(name = "deposit",
                                    value = "{\n  \"amount\": 50.00,\n  \"operationId\": \"11111111-1111-1111-1111-111111111111\"\n}")))
            DepositRequest request
    ) {
        DepositResponse response = depositUseCase.deposit(accountId, request.amount(), request.operationId());
        HttpStatus status = response.applied() ? HttpStatus.CREATED : HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    @PostMapping("/{id}/withdraw")
    @Operation(
            summary = "Withdraw money",
            description = "Withdraw a positive amount from the given account"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Withdraw applied",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = WithdrawResponse.class),
                            examples = @ExampleObject(value = "{\n  \"accountId\": \"6c0e3b06-8d1e-4b5f-8f9a-8d2d7a1a0c00\",\n  \"balance\": 60.00\n}"))),
            @ApiResponse(responseCode = "409", description = "Insufficient funds",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.kata.bankaccount.adapter.in.web.dto.ApiErrorResponse.class),
                            examples = @ExampleObject(value = "{\n  \"code\": \"INSUFFICIENT_FUNDS\",\n  \"message\": \"Insufficient funds\"\n}"))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.kata.bankaccount.adapter.in.web.dto.ApiErrorResponse.class),
                            examples = @ExampleObject(value = "{\n  \"code\": \"VALIDATION_ERROR\",\n  \"message\": \"amount must be > 0\"\n}"))),
            @ApiResponse(responseCode = "404", description = "Account not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.kata.bankaccount.adapter.in.web.dto.ApiErrorResponse.class),
                            examples = @ExampleObject(value = "{\n  \"code\": \"ACCOUNT_NOT_FOUND\",\n  \"message\": \"Account not found\",\n  \"operationId\": \"11111111-1111-1111-1111-111111111111\"\n}")))
    })
    public ResponseEntity<WithdrawResponse> withdraw(
            @Parameter(description = "Account ID") @PathVariable("id") UUID accountId,
            @Valid @org.springframework.web.bind.annotation.RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Withdraw request",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = WithdrawRequest.class),
                            examples = @ExampleObject(name = "withdraw",
                                    value = "{\n  \"amount\": 40.00,\n  \"operationId\": \"11111111-1111-1111-1111-111111111111\"\n}")))
            WithdrawRequest request
    ) {
        var response = withdrawUseCase.withdraw(accountId, request.amount(), request.operationId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/transactions")
    @Operation(
            summary = "List transactions",
            description = "List the account transactions, optionally filtered by time range"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transactions list",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TransactionResponse.class),
                            examples = @ExampleObject(value = "[\n  {\n    \"type\": \"DEPOSIT\",\n    \"amount\": 100.00,\n    \"timestamp\": \"2024-01-01T10:00:00Z\",\n    \"resultingBalance\": 100.00\n  },\n  {\n    \"type\": \"WITHDRAW\",\n    \"amount\": 40.00,\n    \"timestamp\": \"2024-01-02T11:00:00Z\",\n    \"resultingBalance\": 60.00\n  }\n]")))
    })
    public List<TransactionResponse> transactions(
            @Parameter(description = "Account ID") @PathVariable("id") UUID accountId,
            @Parameter(description = "Start of time range (ISO-8601)")
            @RequestParam(value = "from", required = false) Instant from,
            @Parameter(description = "End of time range (ISO-8601)")
            @RequestParam(value = "to", required = false) Instant to
    ) {
        return listTransactionsUseCase.transactions(accountId, from, to);
    }

    @GetMapping(value = "/{id}/statement", produces = {"text/csv", "application/json"})
    @Operation(
            summary = "Export account statement (CSV)",
            description = "Returns the account transactions as CSV lines: date, operation, amount, balanceAfter."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "CSV statement",
                    content = @Content(mediaType = "text/csv",
                            examples = @ExampleObject(value = "date,operation,amount,balanceAfter\n"
                                    + "2024-01-01T12:00:00Z,DEPOSIT,10.00,80.00\n"
                                    + "2024-01-01T11:00:00Z,WITHDRAWAL,30.00,70.00\n"))),
            @ApiResponse(responseCode = "404", description = "Account not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.kata.bankaccount.adapter.in.web.dto.ApiErrorResponse.class),
                            examples = @ExampleObject(value = "{\n  \"code\": \"ACCOUNT_NOT_FOUND\",\n  \"message\": \"Account not found\"\n}")))
    })
    public ResponseEntity<String> statement(
            @Parameter(description = "Account ID") @PathVariable("id") UUID accountId,
            @Parameter(description = "Start date (YYYY-MM-DD)")
            @RequestParam(value = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @Parameter(description = "End date (YYYY-MM-DD)")
            @RequestParam(value = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        String csv = exportStatementUseCase.statementCsv(accountId, from, to);
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("text/csv"))
                .body(csv);
    }
}
