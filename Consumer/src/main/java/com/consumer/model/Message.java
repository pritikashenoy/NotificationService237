package com.consumer.model;
import java.util.*;

public class Message {
    public String cmd;
    public String topic;
    public String data;

    public Message(String c, String t, String d) {
        this.data = d;
        this.cmd = c;
        this.topic = t;
    }
}