package com.kata.bankaccount.adapter.persistence;

import com.kata.bankaccount.adapter.out.persistence.jpa.entity.AccountEntity;
import com.kata.bankaccount.adapter.out.persistence.jpa.repository.AccountJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class AccountLockingIT {

    @Autowired
    AccountJpaRepository accountJpaRepository;
    @Autowired
    TransactionTemplate transactionTemplate;
    @Autowired
    JdbcTemplate jdbcTemplate;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @AfterEach
    void tearDown() {
        executor.shutdownNow();
    }

    @Test
    void lockById_usesPessimisticWrite_selectForUpdate() throws Exception {
        UUID accountId = UUID.randomUUID();
        transactionTemplate.execute(status -> {
            accountJpaRepository.save(new AccountEntity(accountId, BigDecimal.ZERO));
            return null;
        });

        CountDownLatch locked = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);

        // Tx1: acquire the lock and hold it
        executor.submit(() -> transactionTemplate.execute(status -> {
            accountJpaRepository.lockById(accountId).orElseThrow();
            locked.countDown();
            try {
                release.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return null;
        }));

        // Wait until the first transaction has locked the row
        locked.await();

        // Tx2: set a short lock timeout and attempt to acquire the same lock (H2)
        assertThatThrownBy(() -> transactionTemplate.execute(status -> {
            // H2: lock timeout in milliseconds for the current session
            jdbcTemplate.execute("SET LOCK_TIMEOUT 100");
            accountJpaRepository.lockById(accountId).orElseThrow();
            return null;
        }))
            .as("Second lock attempt should timeout due to SELECT FOR UPDATE")
            .isInstanceOfAny(
                    org.springframework.dao.CannotAcquireLockException.class,
                    org.springframework.dao.PessimisticLockingFailureException.class,
                    org.springframework.dao.QueryTimeoutException.class,
                    org.springframework.transaction.TransactionSystemException.class
            )
            .satisfies(ex -> {
                String msg = ex.toString().toLowerCase();
                org.assertj.core.api.Assertions.assertThat(msg).contains("timeout");
            });

        // Release Tx1
        release.countDown();
    }
}
