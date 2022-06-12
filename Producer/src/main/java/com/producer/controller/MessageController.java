package com.producer.controller;


import com.producer.service.ProducerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/kafka")
public final class MessageController {
    private final ProducerService producerService;
    @Value(value = "${spring.kafka.producer.bootstrap-servers}")
    private String bootstrapServer;
    public MessageController(ProducerService producerService) {
        this.producerService = producerService;
    }

    @GetMapping("/info")
    public String getInfo() throws IOException, InterruptedException {
        return bootstrapServer;
    }

        @PostMapping("/publish/low")
    @ResponseStatus(value = HttpStatus.OK)
    public void sendMessageToKafkaTopicLow() throws IOException, InterruptedException {
        producerService.generateLow();
    }

    @PostMapping("/publish/high")
    @ResponseStatus(value = HttpStatus.OK)
    public void sendMessageToKafkaTopicHigh() throws IOException, InterruptedException {
        producerService.generateHigh();
    }

    @PostMapping("/publish/highone")
    @ResponseStatus(value = HttpStatus.OK)
    public void sendMessageToKafkaTopicHighOne() throws IOException, InterruptedException {
        producerService.generateHighOne();
    }




    }