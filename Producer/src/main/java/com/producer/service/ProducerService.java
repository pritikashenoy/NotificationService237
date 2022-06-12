package com.producer.service;

import com.producer.model.Feed;
import com.producer.model.FeedMessage;
import com.producer.model.RSSFeedParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class ProducerService {
    private static final Logger logger = LoggerFactory.getLogger(ProducerService.class);

    @Value("${kafka.producer.topic1.name}")
    private String topic1Name;

    @Value("${kafka.producer.topic2.name}")
    private String topic2Name;

    @Value("${kafka.producer.topic3.name}")
    private String topic3Name;

    @Value("${kafka.producer.topic4.name}")
    private String topic4Name;

    @Value("${kafka.producer.topic5.name}")
    private String topic5Name;

    @Value("${kafka.producer.topic6.name}")
    private String topic6Name;

    @Value("${kafka.producer.topic7.name}")
    private String topic7Name;

    Map<String, String> topics;

    private final KafkaTemplate<Integer, String> kafkaTemplate;

    public ProducerService(KafkaTemplate<Integer, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Async("asyncTaskExecutor")
    public void generateLow() throws IOException, InterruptedException {
        // 1. Start with the slowest rate - 10 messages/5second
        // This URL gives 10 messages at a time  -  we want to send 10 messages per sec
        // run for 5 minutes.
        String url = "http://lorem-rss.herokuapp.com/feed?unit=second&interval=5&length=10";
        String topicName = "lorem10";
        long endTime = System.nanoTime() + TimeUnit.NANOSECONDS.convert(5L, TimeUnit.MINUTES);
        while (System.nanoTime() < endTime) {
            try {
                RSSFeedParser parser = new RSSFeedParser(url);
                Feed feed = parser.readFeed();
                int i = 0;
                for (FeedMessage message : feed.getMessages()) {
                    kafkaTemplate.send(topicName, i++, message.toString());
                    logger.debug("Sending at low rate:" + message.toString());
                }
            } catch (Exception e) {
                logger.debug("URL " + url + " failed!");
                e.printStackTrace();
            }
        }
    }

    @Async("asyncTaskExecutor")
    public void generateHigh() throws IOException, InterruptedException {
        // 1. Start with the fastest rate - 1000 messages/second
        // This URL gives 1000 messages at a time  -  we want to send 1000 messages per sec
        // Run for 5 minutes continuously
        String url = "http://lorem-rss.herokuapp.com/feed?unit=second&length=1000";
        String topicName = "lorem10";
        long endTime = System.nanoTime() + TimeUnit.NANOSECONDS.convert(5L, TimeUnit.MINUTES);
        while (System.nanoTime() < endTime) {
            try {
                RSSFeedParser parser = new RSSFeedParser(url);
                Feed feed = parser.readFeed();
                int i = 0;
                for (FeedMessage message : feed.getMessages()) {
                    kafkaTemplate.send(topicName, i++, message.toString());
                    logger.debug("Sending at high rate:" + message);
                }
            } catch (Exception e) {
                logger.debug("URL " + url + " failed!");
                e.printStackTrace();
            }
        }
    }

    public void generateHighOne() throws IOException, InterruptedException {
        // 1. Start with the fastest rate - 1000 messages/second
        // This URL gives 1000 messages at a time  -  we want to send 1000 messages per sec
        // Run for 5 minutes continuously
        String url = "http://lorem-rss.herokuapp.com/feed?unit=second&length=5";
        String topicName = "lorem10";
        try {
            logger.debug("In try of high one");
            RSSFeedParser parser = new RSSFeedParser(url);
            Feed feed = parser.readFeed();
            int i = 0;
            for (FeedMessage message : feed.getMessages()) {
                kafkaTemplate.send(topicName, i++, message.toString());
                logger.debug("Sending at high rate:" + message);
            }
        } catch (Exception e) {
            logger.debug("URL " + url + " failed!");
            e.printStackTrace();
        }
    }
}
