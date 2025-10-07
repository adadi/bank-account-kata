package com.kata.bankaccount.application.ports.in;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Use case to export an account statement as CSV.
 */
public interface ExportStatementUseCase {

    /**
     * Returns the CSV content for the account statement, with header.
     * Columns: date,operation,amount,balanceAfter
     * from/to are calendar dates (UTC), optional.
     */
    String statementCsv(UUID accountId, LocalDate from, LocalDate to);
}
