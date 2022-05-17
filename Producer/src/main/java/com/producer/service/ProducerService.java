package com.producer.service;
import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.stream.Stream;

import static com.producer.config.KafkaTopicConfig.Topic1;

@Service
public final class ProducerService {
    private static final Logger logger = LoggerFactory.getLogger(ProducerService.class);

    private final KafkaTemplate<Integer, String> kafkaTemplate;
    private final String TOPIC = Topic1;

    public ProducerService(KafkaTemplate<Integer, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    private static Faker faker;

    @EventListener(ApplicationStartedEvent.class)
    public void generate() {
        System.out.println("DEBUG: inside generator");
        faker = Faker.instance();
        // generate weather related notifications
        final Flux<Long> interval = Flux.interval(Duration.ofMillis(1000));
        final Flux<String> weather_desc = Flux.fromStream(Stream.generate(() -> faker.weather().description()));

        // generate stock related notifications
        //final Flux<Long> interval_stock = Flux.interval(Duration.ofMillis(500));
        final Flux<String> stock_desc = Flux.fromStream(Stream.generate(() -> faker.stock().nsdqSymbol()));

        // generate job related notifications
        //final Flux<Long> interval_job = Flux.interval(Duration.ofMillis(250));
        final Flux<String> job_desc = Flux.fromStream(Stream.generate(() -> faker.job().position()));

        Flux.zip(interval, weather_desc, stock_desc, job_desc)
                .subscribe(it ->
                {
                    kafkaTemplate.send("weather", faker.random().nextInt(42), it.getT2());
                    kafkaTemplate.send("stock", faker.random().nextInt(42), it.getT3());
                    kafkaTemplate.send("job", faker.random().nextInt(42), it.getT4());
                });


    }
}



//    public void sendMessage(String message) {
//        logger.info(String.format("$$$$ => Producing message: %s", message));
//
//        ListenableFuture<SendResult<String, String>> future = this.kafkaTemplate.send(TOPIC, message);
//        future.addCallback(new ListenableFutureCallback<>() {
//            @Override
//            public void onFailure(Throwable ex) {
//                logger.info("Unable to send message=[ {} ] due to : {}", message, ex.getMessage());
//            }
//
//            @Override
//            public void onSuccess(SendResult<String, String> result) {
//                logger.info("Sent message=[ {} ] with offset=[ {} ]", message, result.getRecordMetadata().offset());
//            }
//        });
//    }