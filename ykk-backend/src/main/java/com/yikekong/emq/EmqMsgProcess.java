package com.yikekong.emq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yikekong.dto.DeviceInfoDTO;
import com.yikekong.dto.DeviceLocation;
import com.yikekong.es.ESRepository;
import com.yikekong.service.*;
import com.yikekong.util.JsonUtil;
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
public class EmqMsgProcess implements MqttCallback {

    @Autowired
    private EmqClient emqClient;


    @Autowired
    private QuotaService quotaService;

    @Autowired
    private AlarmService alarmService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private GpsService gpsService;

    @Autowired
    private ESRepository esRepository;

    @Autowired
    private NoticeService noticeService;

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
        DeviceInfoDTO deviceInfoDTO = quotaService.analysis(topic, payloadMap);
        if (deviceInfoDTO != null) {
            // Verify and set the device alarm information
            deviceInfoDTO = alarmService.verifyDeviceInfo(deviceInfoDTO);
            // Save the device information
            deviceService.saveDeviceInfo(deviceInfoDTO.getDevice());

            // Save the device quota information to InfluxDB
            quotaService.saveQuotaToInflux(deviceInfoDTO.getQuotaList());
//
//            //指标透传
//            noticeService.quotaTransfer(deviceInfoDTO.getQuotaList());

        }
//
//
//        //解析gps
//        DeviceLocation deviceLocation = gpsService.analysis(topic, payloadMap);
//        if (deviceLocation != null) {
//            System.out.println("gps解析结果：" + JsonUtil.serialize(deviceLocation));
//            esRepository.saveLocation(deviceLocation);
//            noticeService.gpsTransfer(deviceLocation);
//        }


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


