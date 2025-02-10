package com.frauddetectionapplication.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TransactionResponse {
    @JsonProperty("tran_id")
    private String transactionId;
    @JsonProperty("user_id")
    private String userId;
    private String message;
}
