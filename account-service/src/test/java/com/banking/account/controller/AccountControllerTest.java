package com.banking.account.controller;

import com.banking.account.dto.AccountResponse;
import com.banking.account.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
@TestPropertySource(properties = {
    "spring.cloud.discovery.enabled=false",
    "spring.cloud.config.enabled=false"
})
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Test
    @WithMockUser(roles = "USER")
    void shouldGetAccountById() throws Exception {
        AccountResponse response = new AccountResponse(1L, "1234567890", 1L, "John Doe", 
            "SAVINGS", BigDecimal.valueOf(1000), "USD", "ACTIVE", LocalDateTime.now());

        when(accountService.getAccountById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/accounts/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accountNumber").value("1234567890"));
    }
}

