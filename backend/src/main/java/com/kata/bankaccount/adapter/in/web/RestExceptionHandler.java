package com.kata.bankaccount.adapter.in.web;

import com.kata.bankaccount.adapter.in.web.dto.ApiErrorResponse;
import com.kata.bankaccount.domain.exception.AccountNotFoundException;
import com.kata.bankaccount.domain.exception.InsufficientFundsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Maps domain and validation exceptions to HTTP error responses.
 */
@RestControllerAdvice
public class RestExceptionHandler {

    /**
     * Handles bean validation errors (400 BAD_REQUEST).
     * @param ex validation exception
     * @return error payload with code and message
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        var message = ex.getBindingResult().getAllErrors().stream()
                .findFirst()
                .map(err -> err.getDefaultMessage() != null ? err.getDefaultMessage() : "Validation failed")
                .orElse("Validation failed");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiErrorResponse("VALIDATION_ERROR", message, null));
    }

    /**
     * Handles invalid arguments (400 BAD_REQUEST).
     * @param ex thrown exception
     * @return response with BAD_REQUEST status and error payload
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiErrorResponse("BAD_REQUEST", ex.getMessage(), null));
    }

    /**
     * Handles domain insufficient funds (409 CONFLICT).
     * @param ex thrown exception
     * @return response with CONFLICT status and error payload
     */
    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ApiErrorResponse> handleInsufficient(InsufficientFundsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiErrorResponse("INSUFFICIENT_FUNDS", ex.getMessage(), null));
    }

    /**
     * Handles missing account (404 NOT_FOUND).
     * @param ex thrown exception
     * @return response with NOT_FOUND status and error payload
     */
    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleAccountNotFound(AccountNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiErrorResponse("ACCOUNT_NOT_FOUND", ex.getMessage(), ex.getOperationId()));
    }
}
