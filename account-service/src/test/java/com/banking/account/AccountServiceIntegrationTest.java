package com.banking.account;

import com.banking.account.domain.Account;
import com.banking.account.domain.AccountType;
import com.banking.account.domain.Customer;
import com.banking.account.repository.AccountRepository;
import com.banking.account.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@TestPropertySource(properties = {
    "spring.cloud.discovery.enabled=false",
    "spring.cloud.config.enabled=false",
    "eureka.client.enabled=false"
})
class AccountServiceIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void shouldCreateAccountSuccessfully() {
        Customer customer = new Customer("John", "Doe", "john@example.com", "1234567890");
        customer = customerRepository.save(customer);

        Account account = new Account("1234567890", customer, AccountType.SAVINGS, BigDecimal.valueOf(1000), "USD");
        Account saved = accountRepository.save(account);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getAccountNumber()).isEqualTo("1234567890");
    }
}

