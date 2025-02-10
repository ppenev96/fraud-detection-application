package com.frauddetectionapplication.service.impl;

import com.frauddetectionapplication.model.Transaction;
import com.frauddetectionapplication.model.dto.TransactionRequest;
import com.frauddetectionapplication.repository.TransactionRepository;
import com.frauddetectionapplication.service.BlacklistCountryService;
import com.frauddetectionapplication.service.RedisService;
import com.frauddetectionapplication.service.TransactionService;
import com.frauddetectionapplication.utilities.FraudAlertHandler;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Log4j2
public class TransactionServiceImpl implements TransactionService {
    @Value("${max.transactions.per.minute}")
    private Integer maxTransactionsPerMinute;

    @Value("${distance.threshold}")
    private Double distanceThresholdInKM;

    @Value("${time.threshold}")
    private Integer timeThresholdInSeconds;

    private final TransactionRepository transactionRepository;
    private final BlacklistCountryService blacklistCountryService;
    private final RedisService redisService;

    @SneakyThrows
    @KafkaListener(topics = "${kafka.transaction.topic}", groupId = "fraud-group")
    public void consumeTransaction(TransactionRequest transaction) {

        FraudReason fraudTransaction = isFraudTransaction(transaction);

        if (fraudTransaction.fraudulent) {
            String alertMessage = "Fraud Alert! Transaction ID: " + transaction.getTransactionId() + " - Reason: " + fraudTransaction.reason;
            FraudAlertHandler.sendFraudAlert(alertMessage);
        }

        Transaction transactionEntity = Transaction.builder()
                .transactionId(transaction.getTransactionId())
                .userId(transaction.getUserId())
                .country(transaction.getCountry())
                .timestamp(transaction.getTimestamp())
                .amount(transaction.getAmount())
                .isFraudulent(fraudTransaction.fraudulent())
                .longitudeCoordinates(transaction.getLongitudeCoordinates())
                .latitudeCoordinates(transaction.getLatitudeCoordinates())
                .build();

        redisService.saveTransaction(transactionEntity.getUserId(), transaction);

        transactionRepository.save(transactionEntity);
    }

    private FraudReason isFraudTransaction(TransactionRequest transaction) {
        boolean isFraudTransaction = false;
        String reason = null;

        List<TransactionRequest> transactionsForLastMinute = redisService.getRecentTransactions(transaction.getUserId(), 60);

        if (transactionsForLastMinute.size() > maxTransactionsPerMinute) {
            isFraudTransaction = true;
            reason = "More than 10 transactions in the last 1 minute.";
        }

        if(!isFraudTransaction) {
            List<TransactionRequest> transactionsForTimeThreshold = redisService.getRecentTransactions(transaction.getUserId(), timeThresholdInSeconds);

            for (TransactionRequest pastTransaction : transactionsForTimeThreshold) {

                double distanceInKM = calculateDistanceInKM(
                        transaction.getLatitudeCoordinates(), transaction.getLongitudeCoordinates(),
                        pastTransaction.getLatitudeCoordinates(), pastTransaction.getLongitudeCoordinates()
                );

                if (distanceInKM > distanceThresholdInKM &&
                        pastTransaction.getTimestamp().toInstant().isAfter(transaction.getTimestamp().toInstant().minusSeconds(timeThresholdInSeconds))) {
                    isFraudTransaction = true;
                    reason = "User made two transactions from locations > 300 km apart in 30 minutes.";
                    break;
                }
            }
        }

        // Blacklisted country
        if (!isFraudTransaction &&
                blacklistCountryService.getAllBlacklistedCountries().contains(transaction.getCountry())) {
            isFraudTransaction = true;
            reason = String.format("Country: %s is blacklisted.", transaction.getCountry());
        }

        // Transactions in 3 different countries within 10 minutes
        if (!isFraudTransaction) {
            Set<String> countries = redisService.getUserCountries(transaction.getUserId(), 600);
            if (countries.size() >= 3) {
                isFraudTransaction = true;
                reason = "Transactions in 3 different countries within 10 minutes.";
            }
        }

        return new FraudReason(reason, isFraudTransaction);
    }

    //If a user makes two transactions from locations > 300 km apart in 30 minutes, flag as fraud
    //Haversine formula
    private double calculateDistanceInKM(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private record FraudReason(String reason, boolean fraudulent) {
    }
}
