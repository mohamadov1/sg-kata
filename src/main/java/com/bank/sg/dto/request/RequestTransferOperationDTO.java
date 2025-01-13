package com.bank.sg.dto.request;

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
public class RequestTransferOperationDTO {
    @JsonProperty("sender_account_id")
    @NotNull
    @Min(value = 1, message = INVALID_ACCOUNT_ID)
    private Long senderAccountId;

    @JsonProperty("receiver_account_id")
    @NotNull
    @Min(value = 1, message = INVALID_ACCOUNT_ID)
    private Long receiverAccountId;

    @JsonProperty("value")
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false,  message = VALUE_INVALID)
    @Digits(integer = 12, fraction = 2, message = VALUE_FORMAT_INVALID)
    private BigDecimal value;
}
