package com.yikekong.core;

import com.yikekong.emq.EmqClient;
import com.yikekong.service.GpsService;
import com.yikekong.service.QuotaService;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * This class is used to monitor the MQTT server.
 */
@Component
@Slf4j
public class MQTTMonitor {

    @Autowired
    private EmqClient emqClient;

    @Autowired
    private QuotaService quotaService;

    @Autowired
    private GpsService gpsService;


    /**
     * This method is used to initialize the monitor.
     * It's called after the bean is constructed.
     */
    @PostConstruct
    public void init() {
        System.out.println("--------- EMQX monitor started to subscribe topics! ----------");
        // Connect to the MQTT server
        emqClient.connect();

        /*
         * Subscribe to the topics
         * For each subject, subscribe to the topic $queue/subject
         */
        quotaService.getAllSubject().forEach(s -> {
            try {
                // Subscribe to the topic $queue/subject, $queue stands for the shared subscription mode
                emqClient.subscribe("$queue/"+s);
                System.out.println("--------- EMQX monitor subscribed to topic: "+s+" ----------");
            } catch (MqttException e) {
                e.printStackTrace();
            }
        });

//        //gps订阅
//        GPSEntity gpsEntity = gpsService.getGps();  //读取gps配置
//        //共享订阅模式
//        try {
//            emqClient.subscribe("$queue/"+gpsEntity.getSubject());
//        } catch (MqttException e) {
//            e.printStackTrace();
//        }

    }

}
