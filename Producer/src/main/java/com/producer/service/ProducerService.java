package com.producer.service;

import com.producer.model.Feed;
import com.producer.model.FeedMessage;
import com.producer.model.RSSFeedParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.*;


@Service
public class ProducerService {
    private static final Logger logger = LoggerFactory.getLogger(ProducerService.class);
    private final KafkaTemplate<Integer, String> kafkaTemplate;

    public ProducerService(KafkaTemplate<Integer, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // Generates 10 messages every 5 seconds, runs for 5 minutes
    @Async("asyncTaskExecutor")
    public void generateLow() throws IOException, InterruptedException {
        String url = "http://lorem-rss.herokuapp.com/feed?unit=second&interval=5&length=10";
        String topicName = "loremlow";
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

    // Generates 1000 messages per second, runs for 5 minutes
    @Async("asyncTaskExecutor")
    public void generateHigh() throws IOException, InterruptedException {
        String url = "http://lorem-rss.herokuapp.com/feed?unit=second&length=1000";
        String topicName = "loremhigh";
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

    // Standard RSS fetcher, fetches every 5 seconds
    @EventListener(ApplicationStartedEvent.class)
    public void generate() throws IOException, InterruptedException {
        Map<String,String> urls = new HashMap<>();
        urls.put("http://rss.cnn.com/rss/cnn_topstories.rss","news");
        urls.put("https://w1.weather.gov/xml/current_obs/KSNA.rss","weather");

        long endTime = System.nanoTime() + TimeUnit.NANOSECONDS.convert(5L, TimeUnit.MINUTES);
        while (System.nanoTime() < endTime) {
            try {
                for(Map.Entry<String, String> entry : urls.entrySet()) {
                    RSSFeedParser parser = new RSSFeedParser(entry.getKey());
                    Feed feed = parser.readFeed();
                    int i = 0;
                    for (FeedMessage message : feed.getMessages()) {
                        kafkaTemplate.send(entry.getValue(), i, message.toString());
                        logger.debug("Sending " + message.toString());
                        i++;
                    }
                }
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }


    }
}
