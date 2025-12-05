package com.mygame.jms;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * Replaces CorbaMatchManager.
 * Sends asynchronous messages to the JMS Broker to handle match logic.
 */
public class JmsMatchClient {

    private Connection connection;
    private Session session;
    private MessageProducer producer;
    private MessageConsumer consumer;

    public JmsMatchClient() {
        try {
            // 1. Create ConnectionFactory
            ConnectionFactory factory = new ActiveMQConnectionFactory(JmsConstants.BROKER_URL);

            // 2. Create Connection
            connection = factory.createConnection();
            connection.start();

            // 3. Create Session (false = non-transacted, AUTO_ACK)
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // 4. Create Destinations
            Queue requestQueue = session.createQueue(JmsConstants.MATCH_REQUEST_QUEUE);
            Topic eventTopic = session.createTopic(JmsConstants.MATCH_EVENTS_TOPIC);

            // 5. Setup Producer (to send requests)
            producer = session.createProducer(requestQueue);

            // 6. Setup Consumer (to listen for global events)
            consumer = session.createConsumer(eventTopic);
            consumer.setMessageListener(message -> onMessageReceived(message));

            System.out.println("JMS Client Connected.");

        } catch (JMSException e) {
            System.err.println("JMS Connection failed. Is ActiveMQ running?");
            e.printStackTrace();
        }
    }

    /**
     * Sends a "CREATE" request to the queue.
     */
    public void sendCreateMatchRequest(String hostPlayer) {
        if (session == null) return;
        try {
            MapMessage msg = session.createMapMessage();
            msg.setString("type", "CREATE");
            msg.setString("player", hostPlayer);
            msg.setLong("timestamp", System.currentTimeMillis());
            
            producer.send(msg);
            System.out.println("[JMS Client] Sent Create Match Request for " + hostPlayer);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a "JOIN" request to the queue.
     */
    public void sendJoinMatchRequest(String matchId, String playerId) {
        if (session == null) return;
        try {
            MapMessage msg = session.createMapMessage();
            msg.setString("type", "JOIN");
            msg.setString("matchId", matchId);
            msg.setString("player", playerId);
            
            producer.send(msg);
            System.out.println("[JMS Client] Sent Join Request for " + matchId);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    /**
     * Asynchronous callback when a message arrives from the Topic.
     */
    private void onMessageReceived(Message message) {
        try {
            if (message instanceof TextMessage) {
                String text = ((TextMessage) message).getText();
                System.out.println("[JMS Client] Received Event: " + text);
                // In a real app, you would use Platform.runLater() here to update JavaFX
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (connection != null) connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}