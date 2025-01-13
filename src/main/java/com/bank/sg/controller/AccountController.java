package com.bank.sg.controller;

import com.bank.sg.dto.request.RequestCreationAccountDTO;
import com.bank.sg.dto.response.ResponseAccountBalanceDTO;
import com.bank.sg.dto.response.ResponseAccountDTO;
import com.bank.sg.service.AccountService;
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
public class AccountController {

    public static final String ACCOUNT_CREATION_END_POINT_V1 = "/v1/accounts";
    public static final String ACCOUNT_BALANCE_GET_END_POINT_V1 = ACCOUNT_CREATION_END_POINT_V1 + "/{id}";
    private final AccountService accountService;

    @PostMapping(
            path     = ACCOUNT_CREATION_END_POINT_V1,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiOperation(
            value = "Create new account",
            notes = "Create a new bank account for a customer, with an initial deposit amount.\n" +
                    "A single customer may have multiple bank accounts.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Account created."),
            @ApiResponse(code = 404, message = "Customer not found to create a new account."),
            @ApiResponse(code = 400, message = "Invalid request new account information: negative initial amount, negative customer id")
    })
    public ResponseEntity postAccount(@Valid @RequestBody RequestCreationAccountDTO requestCreationAccountDTO) {
        ResponseAccountDTO responseAccountDTO = accountService.createNewAccount(requestCreationAccountDTO);

        URI uri =
                ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(responseAccountDTO.getId())
                        .toUri();

        return ResponseEntity.created(uri).build();
    }



    @GetMapping(
            path     = ACCOUNT_BALANCE_GET_END_POINT_V1,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiOperation(
            value = "Get account balance", notes = "Given an account id, retrieves the balance")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Account balance."),
            @ApiResponse(code = 404, message = "Account not found.")
    })
    public ResponseEntity<ResponseAccountBalanceDTO> getBalance(@PathVariable("id") final long accountId) {
        return ResponseEntity.ok(accountService.retrieveBalance(accountId));
    }
}
