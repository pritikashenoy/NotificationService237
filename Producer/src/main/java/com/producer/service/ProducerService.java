package com.producer.service;

import com.github.javafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

import com.producer.model.*;

@Service
public final class ProducerService {
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


//    @Scheduled(fixedDelay = 1000) // add time in miliseconds
    public void generate() throws IOException, InterruptedException {
        createUrlToTopicMapping();
        String url = "", topicName = topic1Name;
//        while(true) {
//        BufferedReader br = new BufferedReader(new InputStreamReader(new ClassPathResource("classpath:RSSFeed.txt").getInputStream()));

        try {
                url = "http://lorem-rss.herokuapp.com/feed?unit=second&length=10";
//            while ((url = br.readLine()) != null) {
                RSSFeedParser parser = new RSSFeedParser(url);
                Feed feed = parser.readFeed();
                int i = 0;
                for (FeedMessage message : feed.getMessages()) {
                    //topicName = topics.get(url);
                    kafkaTemplate.send(topicName, i, message.toString());
                    logger.debug("Sending " + message.toString());
                    i++;
//                    Thread.sleep((long) (Math.random() * 1000));
                }
//                Thread.sleep((long) (Math.random() * 1000));
//            }
        } catch (Exception e) {
            logger.debug("URL " + url + " failed!");
            e.printStackTrace();
        }
//        }


    }

    // TODO: Find a better way to do this
    void createUrlToTopicMapping() {
        topics = new HashMap<String, String>();
        topics.put("http://lorem-rss.herokuapp.com/feed?unit=second", "lorem1");
        topics.put("https://lorem-rss.herokuapp.com/feed?unit=second&interval=5&length=5", "lorem5");
        topics.put("https://lorem-rss.herokuapp.com/feed?unit=second&interval=10&length=10", "lorem10");
        topics.put("https://lorem-rss.herokuapp.com/feed?unit=second&interval=30&length=30", "lorem30");
        topics.put("https://lorem-rss.herokuapp.com/feed?unit=second&interval=60&length=60", "lorem60");
        topics.put("http://rss.cnn.com/rss/cnn_topstories.rss", "news");
        topics.put("https://w1.weather.gov/xml/current_obs/KSNA.rss", "weather");
    }

}
