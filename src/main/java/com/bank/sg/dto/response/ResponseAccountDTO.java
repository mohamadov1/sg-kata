package com.bank.sg.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseAccountDTO {
    @JsonProperty("customer_id")
    private Long customerId;

    @JsonProperty("account_id")
    private Long id;

    @JsonProperty("balance")
    private BigDecimal balance;

    @JsonProperty("created_at")
    private LocalDateTime creationTimestamp;
}
