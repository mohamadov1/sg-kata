package com.bank.sg.controller;

import com.bank.sg.dto.request.RequestDepositAccountDTO;
import com.bank.sg.dto.request.RequestTransferOperationDTO;
import com.bank.sg.dto.request.RequestWithdrawAccountDTO;
import com.bank.sg.dto.response.ResponseDepositWithdrawTransferDTO;
import com.bank.sg.dto.response.ResponseHistoryOperationsDTO;
import com.bank.sg.service.OperationService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;
import java.net.URI;


@RestController
@RequiredArgsConstructor
public class TransactionController {

    public static final String OPERATION_END_POINT_V1     = "/v1/operations";
    public static final String TRANSFER_GET_END_POINT_V1  = OPERATION_END_POINT_V1  + "/transfer";
    public static final String HISTORY_OPERATION_GET_END_POINT_V1 = OPERATION_END_POINT_V1  + "/history/{accountId}";
    public static final String DEPOSIT_POST_END_POINT_V1  = OPERATION_END_POINT_V1  + "/deposit/{accountId}";
    public static final String WITHDRAW_POST_END_POINT_V1 = OPERATION_END_POINT_V1  + "/withdraw/{accountId}";

    private final OperationService operationService;

    @GetMapping(
            path     = HISTORY_OPERATION_GET_END_POINT_V1,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiOperation(
            value = "Retrieves transactions history for a given account.",
            notes = "Given an account id, retrieves the operations/Deposit/withdraw/transfers where this account has participated.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of operations"),
            @ApiResponse(code = 404, message = "Account not found.")
    })
    public ResponseEntity<ResponseHistoryOperationsDTO> getTransactions(@PathVariable("accountId") final long accountId) {
        return ResponseEntity.ok(operationService.historyOperations(accountId));
    }




    @PostMapping(
            path = DEPOSIT_POST_END_POINT_V1,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiOperation(
            value = "Creates deposit",
            notes = "Deposit money from a customer to his account, is allowed when superior to €0.01.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Transfer created."),
            @ApiResponse(code = 404, message = "Sender/receiver account not found."),
            @ApiResponse(code = 400, message = "Sender/receiver account id negative, Insufficient balance to transfer, Same account used in the transfer operation")
    })
    public ResponseEntity postDeposit(@Valid @RequestBody RequestDepositAccountDTO requestOperationDTO) {
        ResponseDepositWithdrawTransferDTO responseDepositWithdrawTransferDTO = operationService.depositMoney(requestOperationDTO);

        URI uri =
                ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(responseDepositWithdrawTransferDTO.getId())
                        .toUri();

        return ResponseEntity.created(uri).build();
    }


    @PostMapping(
            path = WITHDRAW_POST_END_POINT_V1,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiOperation(
            value = "Create withdraw",
            notes = "Withdraw money from a customer account, is allowed when no overdraft used.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Transfer created."),
            @ApiResponse(code = 404, message = "Sender/receiver account not found."),
            @ApiResponse(code = 400, message = "Sender/receiver account id negative, Insufficient balance to transfer, Same account used in the transfer operation")
    })
    public ResponseEntity postWithdraw(@Valid @RequestBody RequestWithdrawAccountDTO requestWithdrawAccountDTO) {
        ResponseDepositWithdrawTransferDTO responseDepositWithdrawTransferDTO = operationService.withdrawMoney(requestWithdrawAccountDTO);
        URI uri =
                ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(responseDepositWithdrawTransferDTO.getId())
                        .toUri();
        return ResponseEntity.created(uri).build();
    }


    @PostMapping(
            path = TRANSFER_GET_END_POINT_V1,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiOperation(
            value = "Creates new transfer",
            notes = "Transfer amounts between any two accounts, including those owned by different customers.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Transfer created."),
            @ApiResponse(code = 404, message = "Sender/receiver account not found."),
            @ApiResponse(code = 400, message = "Sender/receiver account id negative, Insufficient balance to transfer, Same account used in the transfer operation")
    })
    public ResponseEntity postOperation(@Valid @RequestBody RequestTransferOperationDTO requestTransferOperationDTO) {
        ResponseDepositWithdrawTransferDTO responseDepositWithdrawTransferDTO = operationService.transferOperation(requestTransferOperationDTO);

        URI uri =
                ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(responseDepositWithdrawTransferDTO.getId())
                        .toUri();

        return ResponseEntity.created(uri).build();
    }
}
