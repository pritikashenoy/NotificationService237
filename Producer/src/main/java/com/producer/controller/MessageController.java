//package com.producer.controller;
//
//
//import com.producer.model.Message;
//import com.producer.service.ProducerService;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/kafka")
//public final class MessageController {
//    private final ProducerService producerService;
//
//    public MessageController(ProducerService producerService) {
//        this.producerService = producerService;
//    }
//
//    @PostMapping("/publish")
//    public void sendMessageToKafkaTopic(@RequestBody Message message) {
//        producerService.sendMessage(message.message());
//    }
//    }