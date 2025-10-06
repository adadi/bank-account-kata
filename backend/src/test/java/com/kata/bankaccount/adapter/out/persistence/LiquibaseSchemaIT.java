package com.kata.bankaccount.adapter.out.persistence;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class LiquibaseSchemaIT {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    void tablesExist() {
        assertThat(tableExists("ACCOUNTS")).isTrue();
        assertThat(tableExists("TRANSACTIONS")).isTrue();
        assertThat(tableExists("OPERATIONS")).isTrue();
    }

    @Test
    void uniqueConstraintOnTransactionsOperationId() {
        Integer cnt = jdbcTemplate.queryForObject(
                "select count(*) from INFORMATION_SCHEMA.CONSTRAINTS c " +
                        "join INFORMATION_SCHEMA.CONSTRAINT_COLUMNS cc on c.CONSTRAINT_NAME = cc.CONSTRAINT_NAME and c.TABLE_NAME = cc.TABLE_NAME " +
                        "where c.CONSTRAINT_TYPE = 'UNIQUE' and c.TABLE_NAME = 'TRANSACTIONS' and cc.COLUMN_NAME = 'OPERATION_ID'",
                Integer.class);
        assertThat(cnt).isNotNull();
        assertThat(cnt).isGreaterThanOrEqualTo(1);
    }

    @Test
    void numericColumnsHavePrecision19Scale4() {
        assertNumeric("ACCOUNTS", "BALANCE", 19, 4);
        assertNumeric("TRANSACTIONS", "AMOUNT", 19, 4);
        assertNumeric("TRANSACTIONS", "RESULTING_BALANCE", 19, 4);
    }

    private boolean tableExists(String table) {
        Integer cnt = jdbcTemplate.queryForObject(
                "select count(*) from INFORMATION_SCHEMA.TABLES where TABLE_NAME = ?",
                Integer.class, table);
        return cnt != null && cnt > 0;
    }

    private void assertNumeric(String table, String column, int precision, int scale) {
        var row = jdbcTemplate.queryForMap(
                "select TYPE_NAME, NUMERIC_PRECISION, NUMERIC_SCALE from INFORMATION_SCHEMA.COLUMNS where TABLE_NAME = ? and COLUMN_NAME = ?",
                table, column);
        assertThat(row.get("TYPE_NAME").toString()).matches("(?i)numeric|decimal");
        assertThat(((Number) row.get("NUMERIC_PRECISION")).intValue()).isEqualTo(precision);
        assertThat(((Number) row.get("NUMERIC_SCALE")).intValue()).isEqualTo(scale);
    }
}
