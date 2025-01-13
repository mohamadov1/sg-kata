package com.bank.sg.controller;

import com.bank.sg.repository.CustomerRepository;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class CustomerController {

    public static final String OPERATION_END_POINT_V1 = "/v1/customers";

    private final CustomerRepository customerRepository;

    @GetMapping(
            path     = OPERATION_END_POINT_V1,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiOperation(
            value = "Retrieves all customers.",
            notes = "Retrieves all customers in a bank.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of operations"),
            @ApiResponse(code = 404, message = "Not found.")
    })
    public ResponseEntity getCustomers() {
        return ResponseEntity.ok(customerRepository.findAll());
    }

}
