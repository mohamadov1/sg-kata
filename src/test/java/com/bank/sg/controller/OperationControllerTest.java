package com.bank.sg.controller;

import com.bank.sg.dto.request.RequestDepositAccountDTO;
import com.bank.sg.entity.Account;
import com.bank.sg.entity.Customer;
import com.bank.sg.repository.AccountRepository;
import com.bank.sg.repository.CustomerRepository;
import com.bank.sg.repository.OperationRepository;
import com.bank.sg.service.OperationService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.bank.sg.controller.TransactionController.DEPOSIT_POST_END_POINT_V1;

@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class OperationControllerTest {

    private RestTemplate restTemplate;
    private String url;

    @Autowired
    private OperationRepository operationRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

    @SpyBean
    @Autowired
    private OperationService operationService;

    @LocalServerPort
    private int randomServerPort = 0;

    @BeforeEach
    public void beforeTest() {
        restTemplate = new RestTemplate();
        url = "http://localhost:" +  randomServerPort;
    }



    @Test
    void createDepositInsufficientFundsTest() {
        Customer customer = customerRepository.findById(1L).get();
        BigDecimal someValue = BigDecimal.valueOf(0.01);
        Account receiverAccount = createAccount(customer, BigDecimal.valueOf(1.99));
        RequestDepositAccountDTO requestOperationDTO =
                RequestDepositAccountDTO.builder()
                        .accountId(receiverAccount.getId())
                        .depositAmount(someValue)
                        .build();

        HttpEntity<RequestDepositAccountDTO> request = new HttpEntity<>(requestOperationDTO);
        Throwable throwable = Assertions.catchThrowable(() ->restTemplate.postForEntity(url + DEPOSIT_POST_END_POINT_V1, request, Void.class,receiverAccount.getId()));
        Assertions.assertThat(throwable).isInstanceOf(HttpClientErrorException.class);
        Assertions.assertThat(((HttpClientErrorException) throwable).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }



    @Test
    void createDepositSuccessfulTest() {
        Customer customer = customerRepository.findById(1L).get();
        BigDecimal someValue = BigDecimal.valueOf(100);
        Account receiverAccount = createAccount(customer, BigDecimal.valueOf(1.99));
        RequestDepositAccountDTO requestOperationDTO =
                RequestDepositAccountDTO.builder()
                        .accountId(receiverAccount.getId())
                        .depositAmount(someValue)
                        .build();

        HttpEntity<RequestDepositAccountDTO> request = new HttpEntity<>(requestOperationDTO);
        ResponseEntity<Void> response = restTemplate.postForEntity(url + DEPOSIT_POST_END_POINT_V1, request, Void.class, receiverAccount.getId());
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    }

    @Test
    public Account createAccount(Customer customer, BigDecimal initialDepositAmount) {
        Account account =
                Account.builder()
                        .initialDepositAmount(initialDepositAmount)
                        .creationTimestamp(LocalDateTime.now())
                        .balance(initialDepositAmount)
                        .customer(customer)
                        .build();
        accountRepository.save(account);
        return account;
    }

    public static Long extractId(String locationUrl, String url) {
        url = url + "/";
        return Long.valueOf(locationUrl.replace(url, ""));
    }
}
