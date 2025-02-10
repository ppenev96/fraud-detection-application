package com.frauddetectionapplication.controller;

import com.frauddetectionapplication.model.dto.TransactionRequest;
import com.frauddetectionapplication.model.dto.TransactionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.UUID;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
@Log4j2
public class TransactionController {

    @Value("${kafka.transaction.topic}")
    private String transactionTopic;

    private final KafkaTemplate<String, TransactionRequest> kafkaTemplate;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public TransactionResponse processTransaction(@RequestBody TransactionRequest transaction) {
        transaction.setTransactionId(UUID.randomUUID().toString());
        transaction.setTimestamp(new Timestamp(System.currentTimeMillis()));

        log.info("processTransaction :: Sending transaction: {}", transaction);

        kafkaTemplate.send(transactionTopic, transaction);

        return new TransactionResponse(
                transaction.getTransactionId(),
                transaction.getUserId(),
                "Transaction received and being processed!");
    }
}
