package com.yikekong.core;

import com.yikekong.emq.EmqClient;
import com.yikekong.entity.GPSEntity;
import com.yikekong.service.GpsService;
import com.yikekong.service.QuotaService;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class Monitor {

    @Autowired
    private EmqClient emqClient;

    @Autowired
    private QuotaService quotaService;

    @Autowired
    private GpsService gpsService;


    @PostConstruct
    public void init(){
        System.out.println("初始化方法，订阅主题");
        emqClient.connect();
        //指标订阅
        quotaService.getAllSubject().forEach(s -> {
            //共享订阅模式
            try {
                emqClient.subscribe("$queue/"+s);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        });

        //gps订阅
        GPSEntity gpsEntity = gpsService.getGps();  //读取gps配置
        //共享订阅模式
        try {
            emqClient.subscribe("$queue/"+gpsEntity.getSubject());
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

}
