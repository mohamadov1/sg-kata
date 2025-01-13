package com.bank.sg.dto.request;

import com.bank.sg.configuration.Constants;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

import static com.bank.sg.configuration.Constants.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestDepositAccountDTO {

    @JsonProperty("account_id")
    @NotNull
    @Min(value = 1, message = Constants.ACCOUNT_NOT_FOUND_ERROR)
    private Long accountId;


    @JsonProperty("deposit_amount")
    @DecimalMin(value = "0.01", inclusive = false,  message = INVALID_DEPOSIT_AMOUNT)
    @Digits(integer = 12, fraction = 2, message = INITIAL_AMOUNT_FORMAT_INVALID)
    private BigDecimal depositAmount;

}
