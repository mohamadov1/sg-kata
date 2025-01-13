package com.bank.sg.service;

import com.bank.sg.dto.request.RequestCreationAccountDTO;
import com.bank.sg.dto.response.ResponseAccountBalanceDTO;
import com.bank.sg.dto.response.ResponseAccountDTO;
import com.bank.sg.entity.Account;
import com.bank.sg.entity.Customer;
import com.bank.sg.exception.*;
import com.bank.sg.repository.AccountRepository;
import com.bank.sg.repository.CustomerRepository;
import com.bank.sg.service.mapper.DtoMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import static com.bank.sg.configuration.Constants.*;
import static com.bank.sg.exception.Utils.throwsOnCondition;
import static com.bank.sg.service.mapper.DtoMapper.toAccountEntity;

@Service
@RequiredArgsConstructor
@Log4j2
public class AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    public ResponseAccountDTO createNewAccount(@Valid RequestCreationAccountDTO requestCreationAccountDTO) {
        log.debug("Creating a new account - {}", requestCreationAccountDTO);
        throwsOnCondition(Objects.isNull(requestCreationAccountDTO), InvalidRequestAccountException::new,
                MESSAGE_NULL_REQUEST_ACCOUNT_DTO_ERROR);
        Customer customer = getCustomerById(requestCreationAccountDTO.getCustomerId());
        Account account = toAccountEntity(requestCreationAccountDTO);
        fillMissingFields(account, customer);
        account = accountRepository.save(account);
        log.debug("Created account - {}", account);
        return DtoMapper.toResponseAccountDTO(account);
    }



    public ResponseAccountBalanceDTO retrieveBalance(Long accountId) {
        log.debug("Retrieving balance from accountId = {}", accountId);
        Account account = getAccountById(accountId);
        return ResponseAccountBalanceDTO.builder()
                .id(account.getId())
                .balance(account.getBalance())
                .creationTimestamp(getCurrentTimestamp())
                .build();
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void transfer(@NonNull Account senderAccount, @NonNull Account receiverAccount, BigDecimal value) {
        log.debug("Starting transfer senderAccount: [{}] receiverAccount: [{}] value: [{}]",
                senderAccount, receiverAccount, value);
        throwsOnCondition(senderAccount.getBalance().compareTo(value) < 0,
                InsufficientBalanceException::new,
                String.format(INSUFFICIENT_BALANCE_ERROR, senderAccount.getId()));
        throwsOnCondition(senderAccount.equals(receiverAccount), TransferNotAllowedException::new);
        senderAccount.setBalance(senderAccount.getBalance().subtract(value));
        receiverAccount.setBalance(receiverAccount.getBalance().add(value));
        accountRepository.save(senderAccount);
        accountRepository.save(receiverAccount);
        log.debug("Executed transfer senderAccount: [{}] receiverAccount: [{}] value: [{}]",
                senderAccount, receiverAccount, value);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void deposit(@NonNull Account account, BigDecimal value) {
        log.debug("Starting deposit : [{}] value: [{}]",
                account, value);
        account.setBalance(account.getBalance().add(value));
        accountRepository.save(account);
        log.debug("Executed deposit Account: [{}] value: [{}]",
                account, value);
    }


    @Transactional(propagation = Propagation.MANDATORY)
    public void withdraw(@NonNull Account account, BigDecimal value) {
        log.debug("Starting withdraw : [{}] value: [{}]",
                account, value);
        throwsOnCondition(account.getBalance().compareTo(value) < 0,
                InsufficientBalanceException::new,
                String.format(INSUFFICIENT_BALANCE_ERROR, account.getId()));
        account.setBalance(account.getBalance().subtract(value));
        accountRepository.save(account);
        log.debug("Executed withdraw Account: [{}] value: [{}]",
                account, value);
    }

    Account getAccountById(Long accountId) {
        return accountRepository
                .findById(accountId)
                .orElseThrow(() -> {
                    log.error(ACCOUNT_NOT_FOUND_ERROR, accountId);
                    throw new AccountNotFoundException();
                });
    }

    private void fillMissingFields(Account account, Customer customer) {
        account.setCustomer(customer);
        account.setBalance(account.getInitialDepositAmount());
        account.setCreationTimestamp(getCurrentTimestamp());
    }

    private Customer getCustomerById(long customerId) {
        return customerRepository
                .findById(customerId)
                .orElseThrow(() -> {
                    log.error(CUSTOMER_NOT_FOUND_ID, customerId);
                    throw new CustomerNotFoundException();
                });
    }

    LocalDateTime getCurrentTimestamp() {
            return LocalDateTime.now();
    }
}
