package com.banking.account.repository;

import com.banking.account.domain.Account;
import com.banking.account.domain.AccountType;
import com.banking.account.domain.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
    "spring.cloud.discovery.enabled=false",
    "spring.cloud.config.enabled=false"
})
class AccountRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void shouldFindAccountByNumber() {
        Customer customer = new Customer("Jane", "Smith", "jane@example.com", "9876543210");
        customer = customerRepository.save(customer);

        Account account = new Account("9876543210", customer, AccountType.CHECKING, BigDecimal.valueOf(500), "USD");
        accountRepository.save(account);

        Optional<Account> found = accountRepository.findByAccountNumber("9876543210");

        assertThat(found).isPresent();
        assertThat(found.get().getAccountNumber()).isEqualTo("9876543210");
    }
}

