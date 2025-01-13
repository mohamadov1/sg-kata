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
import com.bank.sg.service.mapper.DtoMapper;
import com.bank.sg.utils.OperationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.bank.sg.configuration.Constants.MESSAGE_NULL_REQUEST_OPERATION_DTO_ERROR;
import static com.bank.sg.exception.Utils.throwsOnCondition;
import static com.bank.sg.service.mapper.DtoMapper.toResponseDepositWithdrawDTO;
import static com.bank.sg.service.mapper.DtoMapper.toResponseOperationDTO;

@Service
@RequiredArgsConstructor
@Log4j2
public class OperationService {

    private final AccountService accountService;
    private final OperationRepository operationRepository;

    @Transactional
    public ResponseDepositWithdrawTransferDTO transferOperation(@Valid RequestTransferOperationDTO requestTransferOperationDTO) {
        log.debug("Creating a new operation - {}", requestTransferOperationDTO);
        throwsOnCondition(Objects.isNull(requestTransferOperationDTO), InvalidRequestOperationException::new,
                MESSAGE_NULL_REQUEST_OPERATION_DTO_ERROR);
        Operation operation = DtoMapper.toTransferOperationEntity(requestTransferOperationDTO);
        Account senderAccount = accountService.getAccountById(requestTransferOperationDTO.getSenderAccountId());
        Account receiverAccount = accountService.getAccountById(requestTransferOperationDTO.getReceiverAccountId());
        accountService.transfer(senderAccount, receiverAccount, requestTransferOperationDTO.getValue());
        fillTransferFields(operation, senderAccount, receiverAccount);
        operation = operationRepository.save(operation);
        logOperation(operation);
        return toResponseOperationDTO(operation);
    }

    @Transactional
    public ResponseDepositWithdrawTransferDTO depositMoney(@Valid RequestDepositAccountDTO requestDepositAccountDTO) {
        log.debug("Creating a new operation - {}", requestDepositAccountDTO);
        throwsOnCondition(Objects.isNull(requestDepositAccountDTO), InvalidRequestOperationException::new,
                MESSAGE_NULL_REQUEST_OPERATION_DTO_ERROR);
        Operation operation = DtoMapper.toDepositOperationEntity(requestDepositAccountDTO);
        Account account = accountService.getAccountById(requestDepositAccountDTO.getAccountId());
        accountService.deposit(account, requestDepositAccountDTO.getDepositAmount());
        fillDepositFields(operation, account);
        operation = operationRepository.save(operation);
        logOperation(operation);
        return toResponseDepositWithdrawDTO(operation);
    }


    @Transactional
    public ResponseDepositWithdrawTransferDTO withdrawMoney(@Valid RequestWithdrawAccountDTO requestWithdrawAccountDTO) {
        log.debug("Creating a Withdraw Money - {}", requestWithdrawAccountDTO);
        throwsOnCondition(Objects.isNull(requestWithdrawAccountDTO), InvalidRequestOperationException::new,
                MESSAGE_NULL_REQUEST_OPERATION_DTO_ERROR);
        Operation operation = DtoMapper.toWithdrawOperationEntity(requestWithdrawAccountDTO);
        Account account = accountService.getAccountById(requestWithdrawAccountDTO.getAccountId());
        accountService.withdraw(account, requestWithdrawAccountDTO.getWithdrawAmount());
        fillWithdrawFields(operation, account);
        operation = operationRepository.save(operation);
        logOperation(operation);
        return toResponseDepositWithdrawDTO(operation);
    }

    public void logOperation(Operation operation) {
        log.debug("Created operation - {}", operation);
    }

    public ResponseHistoryOperationsDTO historyOperations(long accountId) {
        log.debug("Retrieving operations accountId - {}", accountId);
        Account account = accountService.getAccountById(accountId);
        List<ResponseDepositWithdrawTransferDTO> operationDTOList = operationRepository
                .findAllBySenderAccount_IdOrReceiverAccount_IdOrderByOperationDateTimeDesc(account.getId(), account.getId())
                .orElse(new ArrayList<>()).stream().map(DtoMapper::toResponseHistoryDTO)
                .collect(Collectors.toList());
        return ResponseHistoryOperationsDTO.builder()
                .accountId(accountId)
                .operationDTOList(operationDTOList)
                .creationTimestamp(getCurrentTimestamp())
                .build();
    }


    private void fillWithdrawFields(Operation operation, Account account) {
        operation.setSenderAccount(null);
        operation.setReceiverAccount(account);
        operation.setType(OperationType.WITHDRAW.name());
        operation.setSenderAccount(account);
        operation.setOperationDateTime(getCurrentTimestamp());
    }

    private void fillDepositFields(Operation operation, Account account) {
        operation.setSenderAccount(null);
        operation.setReceiverAccount(account);
        operation.setType(OperationType.DEPOSIT.name());
        operation.setOperationDateTime(getCurrentTimestamp());
    }
    private void fillTransferFields(Operation operation, Account senderAccount, Account receiverAccount) {
        operation.setSenderAccount(senderAccount);
        operation.setReceiverAccount(receiverAccount);
        operation.setType(OperationType.TRANSFER.name());
        operation.setOperationDateTime(getCurrentTimestamp());
    }

    LocalDateTime getCurrentTimestamp() {
        return LocalDateTime.now();
    }
}
