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

@Entity
@Table(name = "blacklisted_countries")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlacklistCountry {
    @Id
    private String countryId;
    @Column(name = "country_code")
    private String countryCode;
}
