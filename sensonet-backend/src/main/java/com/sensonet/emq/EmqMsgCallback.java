package com.sensonet.emq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sensonet.dto.PayloadAnalysisResultDTO;
import com.sensonet.service.*;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * This class is used to process the messages received from the MQTT server.
 */
@Component
@Slf4j
public class EmqMsgCallback implements MqttCallback {

    @Autowired
    private EmqClient emqClient;


    @Autowired
    private QuotaService quotaService;

    @Autowired
    private AlarmService alarmService;

    @Autowired
    private DeviceService deviceService;

    /**
     * This method is called when a message arrives from the MQTT server.
     * @param topic The topic the message was published to
     * @param mqttMessage The message
     * @throws Exception Exception
     */
    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        // Message received
        // Convert the message to a string
        String payload = new String(mqttMessage.getPayload());
        // Print the message
        System.out.println("Message received: " + payload);

        // Convert the message to a map
        ObjectMapper mapper = new ObjectMapper();
        Map payloadMap = mapper.readValue(payload, Map.class);

        // Parse the payload
        PayloadAnalysisResultDTO payloadAnalysisResultDTO = quotaService.analysis(topic, payloadMap);
        if (payloadAnalysisResultDTO != null) {
            // Verify and set the device alarm information
            payloadAnalysisResultDTO = alarmService.analyzeAlarmInfo(payloadAnalysisResultDTO);
            // Save the device information to ES
            deviceService.saveAndUpdateDevice(payloadAnalysisResultDTO.getDevice());
            // Save the quota with alarm record information to InfluxDB
            quotaService.saveQuotaToInflux(payloadAnalysisResultDTO.getQuotaWithAlarmRecordList());
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }

    @Override
    public void connectionLost(Throwable throwable) {
        // Connection lost
        System.out.println("--------- EMQX monitor connection lost! ----------");
        // Try to reconnect
        emqClient.connect();
        quotaService.getAllSubject().forEach(s -> {
            // Subscribe to the topic $queue/subject, $queue stands for the shared subscription mode
            try {
                emqClient.subscribe("$queue/" + s);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        });
    }
}


