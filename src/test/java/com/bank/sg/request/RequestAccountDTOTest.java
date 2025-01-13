package com.bank.sg.request;

import com.bank.sg.dto.request.RequestCreationAccountDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.math.BigDecimal;
import java.util.Set;

import static com.bank.sg.configuration.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;

class RequestAccountDTOTest {

    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void validUserIdTest() {
        RequestCreationAccountDTO requestAccountDTO = new RequestCreationAccountDTO(1L, new BigDecimal("0.02"));

        Set<ConstraintViolation<RequestCreationAccountDTO>> violations = validator.validate(requestAccountDTO);

        assertThat(violations.size()).isZero();
    }

    @Test
    void invalidUserIdTest() {
        RequestCreationAccountDTO requestAccountDTO = new RequestCreationAccountDTO(0L, new BigDecimal("10.21"));
        Set<ConstraintViolation<RequestCreationAccountDTO>> violations = validator.validate(requestAccountDTO);
        assertThat(violations.size()).isEqualTo(1);
        violations.forEach(action -> assertThat(action.getMessage())
                .isEqualTo(CUSTOMER_ID_INVALID));
    }

    @Test
    void invalidInitialAmountTest() {
        RequestCreationAccountDTO requestAccountDTO = new RequestCreationAccountDTO(1L, new BigDecimal("0.00"));

        Set<ConstraintViolation<RequestCreationAccountDTO>> violations = validator.validate(requestAccountDTO);

        assertThat(violations.size()).isEqualTo(1);
        violations.forEach(action -> assertThat(action.getMessage())
                .isEqualTo(INVALID_INITIAL_AMOUNT));
    }

    @Test
    void invalidInitialAmountFractionDigitsLessTest() {
        RequestCreationAccountDTO requestAccountDTO = new RequestCreationAccountDTO(1L, new BigDecimal("1.012"));

        Set<ConstraintViolation<RequestCreationAccountDTO>> violations = validator.validate(requestAccountDTO);

        assertThat(violations.size()).isEqualTo(1);
        violations.forEach(action -> assertThat(action.getMessage())
                .isEqualTo(INITIAL_AMOUNT_FORMAT_INVALID));
    }
}
