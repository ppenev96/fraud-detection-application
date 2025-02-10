package com.frauddetectionapplication.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {
    private String transactionId;
    @JsonProperty("user_id")
    private String userId;
    private double amount;
    private String country;
    @JsonProperty("lat_coord")
    private double latitudeCoordinates;
    @JsonProperty("long_coord")
    private double longitudeCoordinates;
    private Timestamp timestamp;
}
