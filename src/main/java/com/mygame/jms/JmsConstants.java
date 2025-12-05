package com.mygame.jms;

public class JmsConstants {
    // The ActiveMQ connection URL (default for local instance)
    public static final String BROKER_URL = "tcp://localhost:61616";
    
    // The Queue where clients send "I want to create/join a match" requests
    public static final String MATCH_REQUEST_QUEUE = "MATCH.REQUEST.QUEUE";
    
    // The Topic where the server broadcasts match events (Start, End)
    public static final String MATCH_EVENTS_TOPIC = "MATCH.EVENTS.TOPIC";
}