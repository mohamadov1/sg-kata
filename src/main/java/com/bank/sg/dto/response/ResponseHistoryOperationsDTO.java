package com.bank.sg.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseHistoryOperationsDTO {

    @JsonProperty("account_id")
    private Long accountId;

    @JsonProperty("operations")
    private List<ResponseDepositWithdrawTransferDTO> operationDTOList = new ArrayList<>();

    @JsonProperty("created_at")
    private LocalDateTime creationTimestamp;
}
