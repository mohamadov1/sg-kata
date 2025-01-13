package com.bank.sg.service;

import com.bank.sg.dto.request.RequestCreationAccountDTO;
import com.bank.sg.dto.response.ResponseAccountBalanceDTO;
import com.bank.sg.dto.response.ResponseAccountDTO;
import com.bank.sg.entity.Account;
import com.bank.sg.entity.Customer;
import com.bank.sg.exception.*;
import com.bank.sg.repository.AccountRepository;
import com.bank.sg.repository.CustomerRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private CustomerRepository customerRepository;

    @Spy
    @InjectMocks
    private AccountService accountService;

    @Test
    void testCreateNewAccountSuccessfully(){
        Long someUserID = 1L;
        String someUserName = "someUserName";
        Long someAccountID = 2L;
        BigDecimal initialAmount = BigDecimal.valueOf(10.01);
        RequestCreationAccountDTO requestAccountDTO =
                RequestCreationAccountDTO.builder()
                        .customerId(someUserID)
                        .initialDepositAmount(initialAmount)
                        .build();
        Customer customer =
                Customer.builder()
                        .id(someUserID)
                        .name(someUserName)
                        .build();

        Account accountWithoutId =
                Account.builder()
                        .initialDepositAmount(initialAmount)
                        .creationTimestamp(LocalDateTime.MIN)
                        .balance(initialAmount)
                        .customer(customer)
                        .build();

        Account accountWithId =
                Account.builder()
                        .id(someAccountID)
                        .initialDepositAmount(initialAmount)
                        .creationTimestamp(LocalDateTime.MIN)
                        .balance(initialAmount)
                        .customer(customer)
                        .build();

        ResponseAccountDTO expectedResponseAccountDTO =
                ResponseAccountDTO.builder()
                        .customerId(someUserID)
                        .id(someAccountID)
                        .balance(initialAmount)
                        .creationTimestamp(LocalDateTime.MIN)
                        .build();

        when(customerRepository.findById(someUserID)).thenReturn(Optional.ofNullable(customer));
        when(accountRepository.save(accountWithoutId)).thenReturn(accountWithId);
        doReturn(LocalDateTime.MIN).when(accountService).getCurrentTimestamp();

        ResponseAccountDTO actualResponseAccountDTO = accountService.createNewAccount(requestAccountDTO);
        Assertions.assertThat(actualResponseAccountDTO).isEqualTo(expectedResponseAccountDTO);
    }


    @Test
    void testCreateNewAccountWithNullRequest() {
        Throwable throwable = Assertions.catchThrowable(() ->accountService.createNewAccount(null));
        Assertions.assertThat(throwable).isInstanceOf(InvalidRequestAccountException.class);
    }

    @Test
    void testCreateNewAccountWithNotFoundUserId() {
        Long someId = 1L;
        RequestCreationAccountDTO requestAccountDTO =
                RequestCreationAccountDTO.builder()
                        .customerId(someId)
                        .build();
        when(customerRepository.findById(someId)).thenReturn(Optional.empty());
        Throwable throwable = Assertions.catchThrowable(() ->accountService.createNewAccount(requestAccountDTO));
        Assertions.assertThat(throwable).isInstanceOf(CustomerNotFoundException.class);
    }

    @Test
    void testGetAccountNotFound() {
        Long someId = 1L;
        when(accountRepository.findById(someId)).thenReturn(Optional.empty());
        Throwable throwable = Assertions.catchThrowable(() ->accountService.getAccountById(someId));
        Assertions.assertThat(throwable).isInstanceOf(AccountNotFoundException.class);
    }

    @Test
    void testSuccessfulTransfer() {
        Account sender =
                Account.builder()
                        .id(1L)
                        .balance(BigDecimal.valueOf(0.01))
                        .build();
        Account receiver =
                Account.builder()
                        .id(2L)
                        .balance(BigDecimal.valueOf(0.99))
                        .build();
        accountService.transfer(sender, receiver, BigDecimal.valueOf(0.01));
        Assertions.assertThat(sender.getBalance()).isEqualTo("0.00");
        Assertions.assertThat(receiver.getBalance()).isEqualTo("1.00");
    }

    @Test
    void testUnsuccessfulTransfer() {
        Account sender =
                Account.builder()
                        .id(1L)
                        .balance(BigDecimal.valueOf(0.00))
                        .build();
        Account receiver =
                Account.builder()
                        .id(2L)
                        .balance(BigDecimal.valueOf(1.00))
                        .build();
        Throwable throwable = Assertions.catchThrowable(() ->accountService.transfer(sender, receiver, BigDecimal.valueOf(0.01)));
        Assertions.assertThat(throwable).isInstanceOf(InsufficientBalanceException.class);
    }

    @Test
    void testNotAllowedSameAccountTransfer() {
        Account sender =
                Account.builder()
                        .id(1L)
                        .balance(BigDecimal.valueOf(1.00))
                        .build();
        Throwable throwable = Assertions.catchThrowable(() ->accountService.transfer(sender, sender, BigDecimal.valueOf(0.01)));
        Assertions.assertThat(throwable).isInstanceOf(TransferNotAllowedException.class);
    }

    @Test
    void testRetrieveBalanceSuccessful() {
        Long someAccountId = 2L;
        BigDecimal someBalance = BigDecimal.valueOf(1.11);
        Customer customer =
                Customer.builder()
                        .id(1L)
                        .name("some name")
                        .build();
        Account account =
                Account.builder()
                        .id(someAccountId)
                        .initialDepositAmount(BigDecimal.valueOf(100))
                        .creationTimestamp(LocalDateTime.now())
                        .balance(someBalance)
                        .customer(customer)
                        .build();

        ResponseAccountBalanceDTO expectedResponse =
                ResponseAccountBalanceDTO.builder()
                        .id(account.getId())
                        .balance(account.getBalance())
                        .creationTimestamp(LocalDateTime.MIN)
                        .build();

        doReturn(LocalDateTime.MIN).when(accountService).getCurrentTimestamp();
        doReturn(account).when(accountService).getAccountById(someAccountId);

        ResponseAccountBalanceDTO actualResponse = accountService.retrieveBalance(someAccountId);

        Assertions.assertThat(actualResponse).isEqualTo(expectedResponse);
    }
}
