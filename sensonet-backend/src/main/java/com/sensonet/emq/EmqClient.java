package com.sensonet.emq;

import com.sensonet.config.EmqConfig;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * This class is used to connect to the MQTT server and subscribe to the topics.
 * This project is an emqx client.
 */
@Component
@Slf4j
public class EmqClient {

    @Autowired
    private EmqConfig emqConfig; // MQTT server configuration

    private MqttClient mqttClient; // MQTT client, used to connect to the MQTT server


    @Autowired
    private EmqMsgCallback emqMsgProcessor;

    /**
     * Connect to the MQTT server
     */
    public void connect() {
        try {
            /*
            The first parameter is the server address, which can be an IP address or a domain name.
            The second parameter is the client ID, which is used to identify the client. (random)
             */
            mqttClient = new MqttClient(emqConfig.getMqttServerUrl(), "monitor." + UUID.randomUUID());
            // Set the callback function of the client
            mqttClient.setCallback(emqMsgProcessor);
            mqttClient.connect();

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * Subscribe to a topic
     *
     * @param topicName the topic to subscribe to
     * @throws MqttException if there is an error subscribing to the topic
     */
    public void subscribe(String topicName) throws MqttException {
        mqttClient.subscribe(topicName);
    }

    /**
     * Publish a message to the MQTT server
     *
     * @param topic the topic of the message
     * @param msg   the content of the message
     */
    public void publish(String topic, String msg) {
        // Create a message object
        MqttMessage mqttMessage = new MqttMessage(msg.getBytes());
        try {
            // Publish the message
            mqttClient.getTopic(topic).publish(mqttMessage);
        } catch (MqttException e) {
            e.printStackTrace();
            log.error("Publish message failed!");
        }
    }

}
