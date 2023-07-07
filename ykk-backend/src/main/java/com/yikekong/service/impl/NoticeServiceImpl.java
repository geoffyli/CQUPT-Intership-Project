package com.yikekong.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Maps;
import com.yikekong.common.SystemDefinition;
import com.yikekong.config.WebHookConfig;
import com.yikekong.dto.AlarmMsg;
import com.yikekong.dto.DeviceLocation;
import com.yikekong.dto.QuotaDTO;
import com.yikekong.emq.EmqClient;
import com.yikekong.service.NoticeService;
import com.yikekong.util.HttpUtil;
import com.yikekong.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.common.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class NoticeServiceImpl implements NoticeService {


    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void quotaTransfer(List<QuotaDTO> quotaDTOList) {

        for( QuotaDTO quotaDTO:quotaDTOList  ){
            //指标透传
            if(!Strings.isNullOrEmpty( quotaDTO.getWebhook() )){
                HttpUtil.httpPost( quotaDTO.getWebhook(),quotaDTO );
            }
            //告警透传
            if( "1".equals( quotaDTO.getAlarm()  ) && !Strings.isNullOrEmpty( quotaDTO.getAlarmWebHook() )  ){
                // key: XXXXX_设备id_告警名称
                String key=SystemDefinition.CYCLE_KEY+"_"+ quotaDTO.getDeviceId()+"_"+quotaDTO.getAlarmName();
                if( redisTemplate.boundValueOps(key ).get()==null  ){
                    HttpUtil.httpPost( quotaDTO.getAlarmWebHook(),quotaDTO );
                    redisTemplate.boundValueOps(key).set( quotaDTO.getStringValue(),  quotaDTO.getCycle() , TimeUnit.MINUTES   );
                }
            }
            //前端推送
            sendAlarm(quotaDTO);

        }
    }

    @Autowired
    private WebHookConfig webHookConfig;

    @Override
    public void onlineTransfer(String deviceId, Boolean online) {
        if(!Strings.isNullOrEmpty( webHookConfig.getOnline())){
            Map<String,Object> map= Maps.newHashMap();
            map.put("deviceId",deviceId);
            map.put("online",online);

            HttpUtil.httpPost(webHookConfig.getOnline() , map);
        }
        if(!online){
            disconnectionAlarm(deviceId);
        }

    }

    @Override
    public void gpsTransfer(DeviceLocation deviceLocation) {
        if(!Strings.isNullOrEmpty( webHookConfig.getGps())){
            HttpUtil.httpPost( webHookConfig.getGps(), deviceLocation);
        }
    }


    @Autowired
    private EmqClient emqClient;

    /**
     * 告警前端推送
     * @param quotaDTO
     */
    private void sendAlarm(QuotaDTO quotaDTO){
        if( !"1".equals( quotaDTO.getAlarm()  )){
            return;
        }
        AlarmMsg alarmMsg=new AlarmMsg();
        BeanUtils.copyProperties( quotaDTO,alarmMsg );
        alarmMsg.setLevel( Integer.parseInt(quotaDTO.getLevel() ) );
        alarmMsg.setOnline(true);

        //发送到emq
        try {
            emqClient.publish("/device/alarm", JsonUtil.serialize(alarmMsg)   );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }

    /**
     * 断网前端推送
     * @param deviceId
     */
    private void disconnectionAlarm(String deviceId){
        //webclient 前端     monitor  服务端
        if( deviceId.startsWith("webclient")  || deviceId.startsWith("monitor") ){
            return;
        }


        AlarmMsg alarmMsg=new AlarmMsg();
        alarmMsg.setDeviceId(deviceId);
        alarmMsg.setLevel(1);
        alarmMsg.setOnline(false);
        alarmMsg.setAlarmName("设备断网");
        alarmMsg.setAlarm(true);
        //发送到emq
        try {
            emqClient.publish("/device/alarm", JsonUtil.serialize(alarmMsg)   );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }




}
