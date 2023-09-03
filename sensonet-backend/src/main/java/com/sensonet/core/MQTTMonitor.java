package com.sensonet.core;

import com.sensonet.emq.EmqClient;
import com.sensonet.service.QuotaService;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashSet;

/**
 * This class is used to monitor the MQTT server.
 * It subscribes to the topics and processes the messages.
 * It's executed after the bean is constructed.
 */
@Component
@Slf4j
public class MQTTMonitor {

    @Autowired
    private EmqClient emqClient;

    @Autowired
    private QuotaService quotaService;


    /**
     * This method is used to initialize the monitor.
     * It's called after the bean is constructed.
     */
    @PostConstruct
    public void init() {
        HashSet<String> topics = new HashSet<>();
        System.out.println("--------- EMQX monitor started to subscribe topics! ----------");
        // Connect to the MQTT server
        emqClient.connect();

        /*
         * Subscribe to the topics
         * For each subject, subscribe to the topic $queue/subject
         */
        for (String subject : quotaService.getAllSubject()) {
            try {
                if (topics.contains(subject)) {
                    continue;
                }
                // Subscribe to the topic $queue/subject, $queue stands for the shared subscription mode
                emqClient.subscribe("$queue/" + subject);
                System.out.println("--------- EMQX monitor subscribed to topic: " + subject + " ----------");
                topics.add(subject);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

}
