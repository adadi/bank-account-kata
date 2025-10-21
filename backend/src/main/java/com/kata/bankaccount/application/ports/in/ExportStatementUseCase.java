package com.kata.bankaccount.application.ports.in;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Use case to export an account statement as CSV.
 */
public interface ExportStatementUseCase {

    /**
     * Generates a CSV statement for the given account and optional date range.
     * Columns: date,operation,amount,balance
     * @param accountId account identifier
     * @param from inclusive start date (optional, UTC)
     * @param to inclusive end date (optional, UTC)
     * @return CSV content with header
     */
    String statementCsv(UUID accountId, LocalDate from, LocalDate to);
}
