package com.mygame.jms;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * Server-side component.
 * Listens to the Request Queue and processes matchmaking.
 * Run this in a separate main method.
 */
public class JmsMatchServer {

    public static void main(String[] args) {
        new JmsMatchServer().start();
    }

    public void start() {
        try {
            ConnectionFactory factory = new ActiveMQConnectionFactory(JmsConstants.BROKER_URL);
            Connection connection = factory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Queue for incoming requests
            Queue requestQueue = session.createQueue(JmsConstants.MATCH_REQUEST_QUEUE);
            // Topic for outgoing broadcasts
            Topic eventTopic = session.createTopic(JmsConstants.MATCH_EVENTS_TOPIC);

            MessageConsumer consumer = session.createConsumer(requestQueue);
            MessageProducer publisher = session.createProducer(eventTopic);

            System.out.println("JMS Match Server Started. Listening on " + JmsConstants.MATCH_REQUEST_QUEUE);

            consumer.setMessageListener(message -> {
                try {
                    if (message instanceof MapMessage) {
                        MapMessage mapMsg = (MapMessage) message;
                        String type = mapMsg.getString("type");

                        if ("CREATE".equals(type)) {
                            String player = mapMsg.getString("player");
                            String newMatchId = "MATCH-" + System.currentTimeMillis();
                            
                            System.out.println("Processing CREATE for " + player + " -> " + newMatchId);
                            
                            // Broadcast that a match was created
                            TextMessage response = session.createTextMessage("MATCH_CREATED:" + newMatchId + ":HOST:" + player);
                            publisher.send(response);
                        } 
                        else if ("JOIN".equals(type)) {
                            String matchId = mapMsg.getString("matchId");
                            String player = mapMsg.getString("player");
                            
                            System.out.println("Processing JOIN for " + player + " -> " + matchId);

                            // Broadcast match start
                            TextMessage response = session.createTextMessage("MATCH_STARTED:" + matchId + ":VS:" + player);
                            publisher.send(response);
                        }
                    }
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            });

            // Keep server alive
            Object lock = new Object();
            synchronized (lock) {
                lock.wait();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}