package com.sensonet.emq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Maps;
import com.sensonet.mapper.entity.QuotaEntity;
import com.sensonet.service.QuotaService;
import com.sensonet.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Component
public class SimulateDataInflux {


    @Autowired
    private QuotaService quotaService;

    @Autowired
    private EmqClient emqClient;

    //@Scheduled(cron = "0/30 * * * * ?")
    public void addData() {
        System.out.println("Simulate data at " + LocalDateTime.now());

        List<QuotaEntity> quotaList = quotaService.list();  // Get all quota definitions
        // Simulate 10 devices
        for (int i = 0; i < 10; i++) {
            String deviceId = 10010 + i + ""; // Generate device ID

            // Get all quota definitions
            for (QuotaEntity quotaEntity : quotaList) {

                Map<String, Object> map = Maps.newHashMap(); // json data from device
                map.put(quotaEntity.getSnKey(), deviceId); // json "sn" field with value

                // Generate a random value for each quota
                Random random = new Random();
                int quotaValue = random.nextInt(40);  //指标值
                map.put(quotaEntity.getValueKey(), quotaValue);

                // Turn the map into a json string
                try {
                    String json = JsonUtil.serialize(map);
                    System.out.println("Publish message: " + json);
                    emqClient.publish(quotaEntity.getSubject(), json); // Publish the message to the MQTT server
                    Thread.sleep(50);
                } catch (JsonProcessingException | InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }


    }


}
