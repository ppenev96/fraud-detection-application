package com.frauddetectionapplication.service.impl;

import com.frauddetectionapplication.model.BlacklistCountry;
import com.frauddetectionapplication.repository.BlacklistCountryRepository;
import com.frauddetectionapplication.service.BlacklistCountryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class BlacklistCountryServiceImpl implements BlacklistCountryService {
    private final BlacklistCountryRepository blacklistCountryRepository;

    @Override
    public Set<String> getAllBlacklistedCountries() {
        if (blacklistCountryRepository.count() == 0) {
            blacklistCountryRepository.saveAll(List.of(
                    new BlacklistCountry(UUID.randomUUID().toString(), "BG"),
                    new BlacklistCountry(UUID.randomUUID().toString(), "UK"),
                    new BlacklistCountry(UUID.randomUUID().toString(), "ROM")
            ));
        }
        return blacklistCountryRepository.findAll()
                .stream()
                .map(BlacklistCountry::getCountryCode)
                .collect(Collectors.toSet());
    }
}
