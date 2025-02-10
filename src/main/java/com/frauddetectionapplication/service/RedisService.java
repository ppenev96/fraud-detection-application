package com.frauddetectionapplication.service;

import com.frauddetectionapplication.model.dto.TransactionRequest;

import java.util.List;
import java.util.Set;

public interface RedisService {

    void saveTransaction(String userId, TransactionRequest transaction);

    List<TransactionRequest> getRecentTransactions(String userId, long timeWindowInSeconds);

    Set<String> getUserCountries(String userId, int timeWindowInSeconds);
}
