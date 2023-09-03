package com.sensonet.emq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Maps;
import com.sensonet.mapper.entity.QuotaEntity;
import com.sensonet.service.QuotaService;
import com.sensonet.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Component
public class SimulateMsgInflux {


    @Autowired
    private QuotaService quotaService;

    @Autowired
    private EmqClient emqClient;

//    @Scheduled(cron = "0/30 * * * * ?")
    public void addData() {
        Random random = new Random();
        int deviceNum = 100;

        System.out.println("Simulate " + deviceNum + " device msg at " + LocalDateTime.now());

        List<QuotaEntity> quotaList = quotaService.list();  // Get all quota definitions
        // Simulate 10 devices
        for (int i = 0; i < deviceNum; i++) {
            Map<String, Object> map = Maps.newHashMap(); // Simulate payload from device
            HashSet<String> topics = new HashSet<>();
            // Generate random string device ID from 00000 to 99999
            String deviceId = String.format("%05d", random.nextInt(500) + 11000);

            // Get all quota definitions
            for (QuotaEntity quotaEntity : quotaList) {
                map.put(quotaEntity.getSnKey(), deviceId); // json "sn" field with value
                // Generate a random value according to the quota type
                switch (quotaEntity.getName()) {
                    case "温度":
                    case "电量": {
                        int quotaValue = random.nextInt(20) + 20;
                        map.put(quotaEntity.getValueKey(), quotaValue);
                        break;
                    }
                    case "湿度": {
                        int quotaValue = random.nextInt(19) + 20;
                        map.put(quotaEntity.getValueKey(), quotaValue);
                        break;
                    }
                    case "压强": {
                        int quotaValue = random.nextInt(300) + 101300;
                        map.put(quotaEntity.getValueKey(), quotaValue);
                        break;
                    }
                    case "电源": {
//                        int choice = random.nextInt(2);
//                        String quotaValue = choice == 0 ? "ON" : "OFF";
                        String quotaValue = "ON";
                        map.put(quotaEntity.getValueKey(), quotaValue);
                        break;
                    }
                    case "G值": {
                        double quotaValue = random.nextDouble() * 2.5;
                        DecimalFormat df = new DecimalFormat("#.##");
                        map.put(quotaEntity.getValueKey(), df.format(quotaValue));
                        break;
                    }
                }
                topics.add(quotaEntity.getSubject());
            }

            // Turn the map into a json string
            try {
                String json = JsonUtil.serialize(map);
                String infoMsg = "Publish message to " + topics + ": " + json;
                System.out.println(infoMsg);
                for (String topic: topics) {
                    emqClient.publish(topic, json); // Publish the message to the MQTT server
                    Thread.sleep(50);
                }
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }


}
