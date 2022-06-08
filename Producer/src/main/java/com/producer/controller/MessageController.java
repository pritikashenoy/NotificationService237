package com.producer.controller;


import com.producer.model.Message;
import com.producer.service.ProducerService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/kafka")
public final class MessageController {
    private final ProducerService producerService;

    public MessageController(ProducerService producerService) {
        this.producerService = producerService;
    }

    @GetMapping("/info")
    public String getInfo() throws IOException, InterruptedException {
        return "Hello kafka";
    }

    @PostMapping("/publish")
    public void sendMessageToKafkaTopic() throws IOException, InterruptedException {
        producerService.generate();
    }
    }