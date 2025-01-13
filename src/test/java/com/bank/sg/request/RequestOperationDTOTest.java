package com.bank.sg.request;

import com.bank.sg.dto.request.RequestTransferOperationDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.math.BigDecimal;
import java.util.Set;

import static com.bank.sg.configuration.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;

class RequestOperationDTOTest {
    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void validDataTest() {
        RequestTransferOperationDTO requestOperationDTO =
                RequestTransferOperationDTO.builder()
                        .senderAccountId(1L)
                        .receiverAccountId(1L)
                        .value(BigDecimal.valueOf(0.01))
                        .build();

        Set<ConstraintViolation<RequestTransferOperationDTO>> violations = validator.validate(requestOperationDTO);

        assertThat(violations.size()).isZero();
    }

    @Test
    void invalidSenderAccountIdTest() {
        RequestTransferOperationDTO requestOperationDTO =
                RequestTransferOperationDTO.builder()
                        .senderAccountId(0L)
                        .receiverAccountId(1L)
                        .value(BigDecimal.valueOf(0.01))
                        .build();

        Set<ConstraintViolation<RequestTransferOperationDTO>> violations = validator.validate(requestOperationDTO);

        assertThat(violations.size()).isEqualTo(1);
        violations.forEach(action -> assertThat(action.getMessage())
                .isEqualTo(INVALID_ACCOUNT_ID));
    }

    @Test
    void invalidReceiverAccountIdTest() {
        RequestTransferOperationDTO requestOperationDTO =
                RequestTransferOperationDTO.builder()
                        .senderAccountId(1L)
                        .receiverAccountId(-1L)
                        .value(BigDecimal.valueOf(0.01))
                        .build();

        Set<ConstraintViolation<RequestTransferOperationDTO>> violations = validator.validate(requestOperationDTO);

        assertThat(violations.size()).isEqualTo(1);
        violations.forEach(action -> assertThat(action.getMessage())
                .isEqualTo(INVALID_ACCOUNT_ID));
    }

    @Test
    void invalidValueTest() {
        RequestTransferOperationDTO requestOperationDTO =
                RequestTransferOperationDTO.builder()
                        .senderAccountId(1L)
                        .receiverAccountId(1L)
                        .value(BigDecimal.valueOf(0))
                        .build();

        Set<ConstraintViolation<RequestTransferOperationDTO>> violations = validator.validate(requestOperationDTO);

        assertThat(violations.size()).isEqualTo(1);
        violations.forEach(action -> assertThat(action.getMessage())
                .isEqualTo(VALUE_INVALID));
    }

    @Test
    void invalidInitialAmountFractionDigitsLessTest() {
        RequestTransferOperationDTO requestOperationDTO =
                RequestTransferOperationDTO.builder()
                        .senderAccountId(1L)
                        .receiverAccountId(1L)
                        .value(new BigDecimal("0.001"))
                        .build();

        Set<ConstraintViolation<RequestTransferOperationDTO>> violations = validator.validate(requestOperationDTO);

        assertThat(violations.size()).isEqualTo(1);
        violations.forEach(action -> assertThat(action.getMessage())
                .isEqualTo(VALUE_FORMAT_INVALID));
    }

}
