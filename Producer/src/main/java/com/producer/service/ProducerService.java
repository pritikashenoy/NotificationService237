package com.producer.service;

import com.github.javafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.stream.Stream;


@Service
public final class ProducerService {
    private static final Logger logger = LoggerFactory.getLogger(ProducerService.class);

    @Value("${kafka.producer.topic1.name}")
    private String topic1Name;

    @Value("${kafka.producer.topic2.name}")
    private String topic2Name;

    @Value("${kafka.producer.topic3.name}")
    private String topic3Name;

    private final KafkaTemplate<Integer, String> kafkaTemplate;

    public ProducerService(KafkaTemplate<Integer, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    private static Faker faker;

    @EventListener(ApplicationStartedEvent.class)
    public void generate() {
        System.out.println("Started producer...");
        faker = Faker.instance();

        final Flux<Long> interval = Flux.interval(Duration.ofMillis(1000));
        // generate weather related notifications
        final Flux<String> weather_desc = Flux.fromStream(Stream.generate(() -> faker.weather().description()));

        // generate stock related notifications
        final Flux<String> stock_desc = Flux.fromStream(Stream.generate(() -> faker.stock().nsdqSymbol()));

        // generate job related notifications
        final Flux<String> job_desc = Flux.fromStream(Stream.generate(() -> faker.job().position()));

        Flux.zip(interval, weather_desc, stock_desc, job_desc)
                .subscribe(it ->
                {
                    kafkaTemplate.send(topic1Name, faker.random().nextInt(42), it.getT2());
                    kafkaTemplate.send(topic2Name, faker.random().nextInt(42), it.getT3());
                    kafkaTemplate.send(topic3Name, faker.random().nextInt(42), it.getT4());
                });


    }
}
