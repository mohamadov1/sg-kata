package com.bank.sg.service;

import com.bank.sg.dto.request.RequestDepositAccountDTO;
import com.bank.sg.dto.request.RequestTransferOperationDTO;
import com.bank.sg.dto.request.RequestWithdrawAccountDTO;
import com.bank.sg.dto.response.ResponseDepositWithdrawTransferDTO;
import com.bank.sg.dto.response.ResponseHistoryOperationsDTO;
import com.bank.sg.entity.Account;
import com.bank.sg.entity.Operation;
import com.bank.sg.exception.InvalidRequestOperationException;
import com.bank.sg.repository.OperationRepository;
import com.bank.sg.utils.OperationType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class OperationsBankServiceTest {

    @Mock
    private AccountService accountService;

    @Mock
    private OperationRepository operationRepository;

    @Spy
    @InjectMocks
    private OperationService operationService;


    @Test
    void testDepositOperationSuccessfully() {
        BigDecimal someValue = BigDecimal.valueOf(0.2);

        Account receiver = Account.builder().id(2L).balance(BigDecimal.valueOf(0.99)).build();

        RequestDepositAccountDTO requestOperationDTO =
                RequestDepositAccountDTO.builder()
                        .accountId(receiver.getId())
                        .depositAmount(someValue).build();

        Operation operationResult =
                Operation.builder().id(3L).senderAccount(null).receiverAccount(receiver).value(someValue)
                        .type(OperationType.DEPOSIT.name())
                        .operationDateTime(LocalDateTime.MIN).build();

        ResponseDepositWithdrawTransferDTO expectedResponseOperationDTO =
                ResponseDepositWithdrawTransferDTO.builder()
                        .id(operationResult.getId())
                        .senderAccountId(null)
                        .typeOperation(OperationType.DEPOSIT.name())
                        .receiverAccountId(receiver.getId()).value(someValue)
                        .creationTimestamp(operationResult.getOperationDateTime()).build();

        when(accountService.getAccountById(receiver.getId())).thenReturn(receiver);
        when(operationRepository.save(Mockito.any(Operation.class))).thenReturn(operationResult);
        when(operationService.getCurrentTimestamp()).thenReturn(LocalDateTime.MIN);

        ResponseDepositWithdrawTransferDTO actualResponseOperationDTO = operationService.depositMoney(requestOperationDTO);

        Assertions.assertThat(actualResponseOperationDTO).isEqualTo(expectedResponseOperationDTO);
    }


    @Test
    void testWithdrawOperationSuccessfully() {
        BigDecimal someValue = BigDecimal.valueOf(0.2);

        Account account = Account.builder().id(2L).balance(BigDecimal.valueOf(0.99)).build();

        RequestWithdrawAccountDTO requestOperationDTO =
                RequestWithdrawAccountDTO.builder()
                        .accountId(account.getId())
                        .withdrawAmount(someValue).build();

        Operation operationResult =
                Operation.builder().id(3L).senderAccount(null).receiverAccount(account).value(someValue)
                        .type(OperationType.WITHDRAW.name())
                        .operationDateTime(LocalDateTime.MIN).build();

        ResponseDepositWithdrawTransferDTO expectedResponseOperationDTO =
                ResponseDepositWithdrawTransferDTO.builder()
                        .id(operationResult.getId())
                        .senderAccountId(null)
                        .typeOperation(OperationType.WITHDRAW.name())
                        .receiverAccountId(account.getId()).value(someValue)
                        .creationTimestamp(operationResult.getOperationDateTime()).build();

        when(accountService.getAccountById(account.getId())).thenReturn(account);
        when(operationRepository.save(Mockito.any(Operation.class))).thenReturn(operationResult);
        when(operationService.getCurrentTimestamp()).thenReturn(LocalDateTime.MIN);

        ResponseDepositWithdrawTransferDTO actualResponseOperationDTO = operationService.withdrawMoney(requestOperationDTO);

        Assertions.assertThat(actualResponseOperationDTO).isEqualTo(expectedResponseOperationDTO);
    }

    @Test
    void testTransferOperationSuccessfully() {
        BigDecimal someValue = BigDecimal.valueOf(0.2);

        Account sender = Account.builder().id(1L).balance(BigDecimal.valueOf(1.01)).build();
        Account receiver = Account.builder().id(2L).balance(BigDecimal.valueOf(0.99)).build();

        RequestTransferOperationDTO requestOperationDTO =
                RequestTransferOperationDTO.builder()
                        .senderAccountId(sender.getId())
                        .receiverAccountId(receiver.getId())
                        .value(someValue).build();

        Operation operationResult =
                Operation.builder().id(3L).senderAccount(sender).receiverAccount(receiver).value(someValue)
                        .type(OperationType.TRANSFER.name())
                        .operationDateTime(LocalDateTime.MIN).build();

        ResponseDepositWithdrawTransferDTO expectedResponseOperationDTO =
                ResponseDepositWithdrawTransferDTO.builder()
                        .id(operationResult.getId())
                        .senderAccountId(sender.getId())
                        .typeOperation(OperationType.TRANSFER.name())
                        .receiverAccountId(receiver.getId()).value(someValue)
                        .creationTimestamp(operationResult.getOperationDateTime()).build();

        when(accountService.getAccountById(sender.getId())).thenReturn(sender);
        when(accountService.getAccountById(receiver.getId())).thenReturn(receiver);
        when(operationRepository.save(Mockito.any(Operation.class))).thenReturn(operationResult);
        when(operationService.getCurrentTimestamp()).thenReturn(LocalDateTime.MIN);

        ResponseDepositWithdrawTransferDTO actualResponseOperationDTO = operationService.transferOperation(requestOperationDTO);

        Assertions.assertThat(actualResponseOperationDTO).isEqualTo(expectedResponseOperationDTO);
    }

    @Test
    void testWithdrawMoneyWithNullRequest() {
        Throwable throwable = Assertions.catchThrowable(() ->operationService.withdrawMoney(null));
        Assertions.assertThat(throwable).isInstanceOf(InvalidRequestOperationException.class);
    }

    @Test
    void testHistoryOperationsEmpty() {
        Account sender = Account.builder().id(1L).build();

        when(operationRepository
                        .findAllBySenderAccount_IdOrReceiverAccount_IdOrderByOperationDateTimeDesc(
                                sender.getId(), sender.getId())).thenReturn(Optional.empty());
        when(accountService.getAccountById(sender.getId())).thenReturn(sender);

        ResponseHistoryOperationsDTO operations = operationService.historyOperations(sender.getId());
        Assertions.assertThat(operations.getAccountId()).isEqualTo(sender.getId());
        Assertions.assertThat(operations.getOperationDTOList().isEmpty()).isTrue();
    }
}
