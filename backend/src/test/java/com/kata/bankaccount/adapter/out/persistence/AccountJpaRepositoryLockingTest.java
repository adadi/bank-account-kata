package com.kata.bankaccount.adapter.out.persistence;

import com.kata.bankaccount.adapter.out.persistence.jpa.repository.AccountJpaRepository;
import jakarta.persistence.LockModeType;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.Lock;

import java.lang.reflect.Method;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AccountJpaRepositoryLockingTest {

    @Test
    void lockById_isAnnotatedWithPessimisticWrite() throws NoSuchMethodException {
        Method m = AccountJpaRepository.class.getMethod("lockById", UUID.class);
        Lock lock = m.getAnnotation(Lock.class);
        assertThat(lock).as("@Lock present on lockById").isNotNull();
        assertThat(lock.value()).isEqualTo(LockModeType.PESSIMISTIC_WRITE);
    }
}

