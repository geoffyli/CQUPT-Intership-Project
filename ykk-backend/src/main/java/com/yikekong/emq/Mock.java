package com.yikekong.emq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Maps;
import com.yikekong.entity.QuotaEntity;
import com.yikekong.service.QuotaService;
import com.yikekong.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Component
public class Mock {


    @Autowired
    private QuotaService quotaService;

    @Autowired
    private EmqClient emqClient;

//    @Scheduled(cron = "0/10 * * * * ?")
//    public void addDatas(){
//        System.out.println(LocalDateTime.now()+"模拟数据产生");
//
//        List<QuotaEntity> quotaList = quotaService.list();  //获取所有指标定义
//        //模拟10台设备
//        for(int i=0;i<10;i++){
//            String deviceId=100000+i+""; //设备编号
//
//            for( QuotaEntity quotaEntity:quotaList ){
//
//                Map<String,Object> map= Maps.newHashMap();//报文
//                map.put(quotaEntity.getSnKey(),deviceId); //设备编号
//
//                //产生随机数
//                Random random=new Random();
//                int quotaValue = random.nextInt(40);  //指标值
//                map.put(quotaEntity.getValueKey(),quotaValue);
//
//                try {
//                    String json = JsonUtil.serialize(map);
//                    emqClient.publish( quotaEntity.getSubject(), json);
//                    Thread.sleep(20);
//                } catch (JsonProcessingException e) {
//                    e.printStackTrace();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//
//        }
//
//
//    }


}
