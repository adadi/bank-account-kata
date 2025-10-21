package com.kata.bankaccount;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot entry point of the Bank Account application.
 */
@SpringBootApplication
public class BankAccountApplication {
    
    /**
     * Launches the Spring Boot application.
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(BankAccountApplication.class, args);
    }
} 
