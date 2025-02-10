package com.frauddetectionapplication.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frauddetectionapplication.model.dto.TransactionRequest;
import com.frauddetectionapplication.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class RedisServiceImpl implements RedisService {
    private static final String TRANSACTION_KEY_PREFIX = "user:transactions:";
    private static final int TRANSACTION_EXPIRATION_TIME = 31;
    private static final int TRANSACTION_COUNTRY_EXPIRATION_TIME = 31;
    private static final String USER_COUNTRIES_KEY_PREFIX = "user:countries:";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    @Override
    public void saveTransaction(String userId, TransactionRequest transaction) {

        String key = TRANSACTION_KEY_PREFIX + userId;

        double score = transaction.getTimestamp().toInstant().toEpochMilli();

        String transactionData = objectMapper.writeValueAsString(transaction);

        redisTemplate.opsForZSet().add(key, transactionData, score);
        redisTemplate.expire(key, TRANSACTION_EXPIRATION_TIME, TimeUnit.MINUTES);

        String userCountriesKey = USER_COUNTRIES_KEY_PREFIX + userId;
        redisTemplate.opsForSet().add(userCountriesKey, transaction.getCountry());

        redisTemplate.expire(userCountriesKey, TRANSACTION_COUNTRY_EXPIRATION_TIME, TimeUnit.MINUTES);
    }

    @Override
    public List<TransactionRequest> getRecentTransactions(String userId, long timeWindowInSeconds) {

        String key = TRANSACTION_KEY_PREFIX + userId;

        long currentTime = Instant.now().toEpochMilli();
        long windowStartTime = currentTime - (timeWindowInSeconds * 1000);

        Set<String> transactions = redisTemplate.opsForZSet().rangeByScore(key, windowStartTime, currentTime);

        return Optional.ofNullable(transactions)
                .orElse(new HashSet<>())
                .stream()
                .map(transaction -> {
                    try {
                        return objectMapper.readValue(transaction, TransactionRequest.class);
                    } catch (IOException e) {
                        log.error("getRecentTransactions :: Exception occurred", e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public Set<String> getUserCountries(String userId, int timeWindowInSeconds) {

        String userCountriesKey = USER_COUNTRIES_KEY_PREFIX + userId;

        return redisTemplate.opsForSet().members(userCountriesKey);
    }
}
