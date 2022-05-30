package com.consumer.service;

import com.consumer.model.Message;
import com.consumer.model.Mail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.kafka.event.*;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.springframework.core.io.*;
import java.util.*;
import java.net.*;
import java.io.*;
import javax.mail.*;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import java.util.stream.Collectors;
import java.nio.charset.*;
import java.util.concurrent.LinkedBlockingQueue;

// Polls its queue and creates a mail content for each message
// Inserts into a database
// A builder thread has a queue and a run function
@Component
class BuilderThread extends Thread {

    // To log the received messages
    private static final Logger LOGGER = LoggerFactory.getLogger(BuilderThread.class);
    Queue<Message> queue;
    boolean stopMe;

    // To send mails for the notifications received by the consumer service
    @Autowired
    private MailService mailService;

    // Mail Service set up configurations
    @Value("${spring.mail.host}")
    private String smtpHost;

    @Value("${spring.mail.username}")
    private String smtpUsername;

    @Value("${spring.mail.password}")
    private String smtpPassword;

    @Value("${spring.mail.port}")
    private String smtpPort;

    @Value("${spring.mail.properties.mail.smtp.auth}")
    private String smtpAuth;

    // A session for the mail service
    // TBD: Even for multiple subscribers, this login will be same, only recipients vary, which can be set in mimemessage in MailService,
    // so this should work
    private Session session;
    private Transport transport;

    // Topic to subscription list mapping
    private Map<String, Set<String>> subscriptions;

    // TODO: Ring Buffer of mails, using a simple queue for now
    private Queue<Mail> pendingMails;

    public BuilderThread() {
        this.queue = new LinkedBlockingQueue<>();
        this.pendingMails = new LinkedList<Mail>();
        this.stopMe = false;
        try {
            // 2. Create subscription list
            setUpSubscriptions();
            // 3. Set up transport to send mails to the subscribers
            setUpTransport();
        } catch(Exception e)
        {
            LOGGER.debug("Setup failed.");
            e.printStackTrace();
        }
        start();
    }

    @Override
    public void run() {
        while (!this.stopMe) {
            Message message;
            if (!queue.isEmpty()) {
                message = queue.poll();
                //dispatch(message);
                if (message.cmd.equals("STOP")) {
                    try {
                        transport.close();
                    } catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                    this.stopMe = true;
                }
                else if(message.cmd.equals("BUILD"))
                {
                    build(message);
                }
                else if(message.cmd.equals("DUMP"))
                {
                    dump();
                }
            } else {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void build(Message message) {
        // or populate hashmap at start of application, pass a pointer to every BuilderThread if possible or dump thread
        // after a batch size of twenty , put the hashmap in dumpthread , dump thread polls every 1 minute
        // TODO: Form mail and save in database
        Mail mail = new Mail();
        mail.setMailFrom("cs237uci@gmail.com");
        List<String> subsList = new ArrayList<String>(subscriptions.get(message.topic));
        mail.setMailTo(subsList);
        mail.setMailSubject(message.topic + " Notification");
        mail.setMailContent(message.data);
        // TODO: Store this mail somewhere
        pendingMails.add(mail);
        //mailService.sendEmail(mail, session, transport);
    }

    private void dump()
    {
        int mailCounter = 0;
        while(!pendingMails.isEmpty() && mailCounter < 20)
        {
            Mail mail = pendingMails.remove();
            mailService.sendEmail(mail, session, transport);
        }
    }
    public void send(Message message)
    {
        queue.offer(message);
    }

    private void setUpSubscriptions() throws Exception {
        LOGGER.debug("In setup subscriptions phase");
        try {
            subscriptions = new HashMap<String, Set<String>>();
            InputStream subInputStream = new ClassPathResource("classpath:subscribers.txt").getInputStream();
            InputStream topicInputStream = new ClassPathResource("classpath:topics.txt").getInputStream();
            List<String> subscribers = new BufferedReader(new InputStreamReader(subInputStream,
                    StandardCharsets.UTF_8)).lines().collect(Collectors.toList());
            List<String> topics = new BufferedReader(new InputStreamReader(topicInputStream,
                    StandardCharsets.UTF_8)).lines().collect(Collectors.toList());

            Random random = new Random();
            int min = 1, max = subscribers.size();
            for (String topic : topics) {
                int numSubs = random.nextInt(max - min) + min;
                Collections.shuffle(subscribers);
                Set<String> randomSubsSet = new HashSet<String>(subscribers.subList(0, numSubs - 1));
                subscriptions.put(topic, randomSubsSet);
            }
            LOGGER.debug("Created topic subscriptions");
        } catch(Exception e)
        {
            LOGGER.debug("Topic subscription mapping failed");
            e.printStackTrace();
        }
    }

    @Bean
    private void setUpTransport() throws Exception {
        LOGGER.debug("In setup transport phase");
        Properties props = new Properties();
        props.put("mail.smtp.host", smtpHost); //SMTP Host
        props.put("mail.smtp.port", smtpPort); //TLS Port
        props.put("mail.smtp.auth", smtpAuth); //enable authentication
        props.put("mail.smtp.starttls.enable", smtpAuth); //enable STARTTLS
        final String fromEmail = smtpUsername + "@gmail.com";
        Authenticator auth = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, smtpPassword);
            }
        };
        session = Session.getInstance(props, auth);
        session.setDebug(true);
        try {
            LOGGER.debug("Creating transport for the consumer service" + " " + fromEmail);
            transport = session.getTransport("smtp");
            LOGGER.debug("Creating transport connection for the consumer service");
            transport.connect(smtpHost, 587, smtpUsername, smtpPassword);
        } catch(Exception e)
        {
            LOGGER.debug("Transport creation OR connection failed");
            e.printStackTrace();
        }
    }

}
// To signal the builder thread that it is time to send
@Component
class SignalThread extends Thread {

    private BuilderThread bt;
    public SignalThread(BuilderThread bthread) {
        this.bt = bthread;
        start();
    }

    @Override
    public void run(){
        // just wakeup every 1 minute and signal the builder thread
        try {
            Thread.sleep(60000);
        } catch(Exception e)
        {
            e.printStackTrace();
        }
        if(bt != null)
            bt.send(new Message("DUMP", "", ""));
    }
}
@Service
public class ConsumerService {
    // To log the received messages
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerService.class);

    @Value("${kafka.consumer.group1.topic}")
    private String group1Topic;

    @Value("${kafka.consumer.group2.topic}")
    private String group2Topic;

    @Value("${kafka.consumer.group3.topic}")
    private String group3Topic;

    // Builder thread to form a mail from the consumed message
    private BuilderThread bt;

    private void consume(String data, String topic) {
        LOGGER.info("Received message='{}'", topic + ": " + data);
        // TBD: Later, Create a hash for the 2 strings, assign data and topic OR create a message class and send to the builder thread number's queue
        // Get the set of subscribers for this topic
        if(bt != null)
        {
            bt.send(new Message("BUILD", topic, data));
        }
    }

    //@EventListener(ConsumerStartedEvent.class)
    @EventListener(ApplicationStartedEvent.class)
    public void setUp() throws Exception {
        LOGGER.debug("In setup phase");
        // 1. TODO: Create number of builder threads = number of processors + 1
        // Just one builder thread for now, to separate the processing
        bt = new BuilderThread();
        SignalThread signalThread = new SignalThread(bt);
    }

    //@EventListener(ConsumerStoppedEvent.class)
    @EventListener(ContextClosedEvent.class)
    public void destroy() throws MessagingException {
        LOGGER.debug("Closing transport for the consumer service");
        if(bt != null)
            bt.send(new Message("STOP","",""));
        //transport.close();
    }

    // Listener for group 1
    @org.springframework.kafka.annotation.KafkaListener(topics = "${kafka.consumer.group1.topic}", concurrency = "${kafka.consumer.group1.consumers}", groupId = "${kafka.consumer.group1.id}")
    public void group1Listener(String data) {consume(data, group1Topic);}

    // Listener for group 2
    @org.springframework.kafka.annotation.KafkaListener(topics = "${kafka.consumer.group2.topic}", concurrency = "${kafka.consumer.group2.consumers}", groupId = "${kafka.consumer.group2.id}")
    public void group2Listener(String data) {
        consume(data, group2Topic);
    }

    // Listener for group 3
    @org.springframework.kafka.annotation.KafkaListener(topics = "${kafka.consumer.group3.topic}", concurrency = "${kafka.consumer.group3.consumers}", groupId = "${kafka.consumer.group3.id}")
    public void group3Listener(String data) {
        consume(data, group3Topic);
    }
}


