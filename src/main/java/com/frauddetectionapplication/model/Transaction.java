package com.frauddetectionapplication.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    private String transactionId;
    @Column(name = "user_id")
    private String userId;
    @Column
    private double amount;
    @Column
    private String country;
    @Column(name = "latitude_coordinates")
    private double latitudeCoordinates;
    @Column(name = "longitude_coordinates")
    private double longitudeCoordinates;
    @Column(name = "is_fraudulent")
    private boolean isFraudulent;
    @Column
    private Timestamp timestamp;
}
