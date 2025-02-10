package com.frauddetectionapplication.repository;

import com.frauddetectionapplication.model.BlacklistCountry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlacklistCountryRepository extends JpaRepository<BlacklistCountry, Long> {
}
