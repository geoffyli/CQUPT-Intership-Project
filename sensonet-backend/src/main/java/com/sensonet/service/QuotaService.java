package com.sensonet.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import com.sensonet.dto.PayloadAnalysisResultDTO;
import com.sensonet.dto.QuotaWithAlarmRecordDTO;
import com.sensonet.dto.QuotaInfoDTO;
import com.sensonet.mapper.entity.QuotaEntity;

import java.util.List;
import java.util.Map;

public interface QuotaService extends IService<QuotaEntity>{

    IPage<QuotaEntity> queryPage(Long page, Long pageSize,String name);
    List<String> getAllSubject();


    /**
     * Analysis the quota info from the payload
     * @param topic The MQTT topic
     * @param payloadMap The payload map from the msg
     * @return The analysis result
     */
    PayloadAnalysisResultDTO analysis(String topic, Map<String,Object> payloadMap);


    /**
     * Save the quota info to influxdb
     * @param quotaWithAlarmRecordDTOList The quota info list
     */
    void saveQuotaToInflux(List<QuotaWithAlarmRecordDTO> quotaWithAlarmRecordDTOList);


    /**
     * Get the last quota list by device id
     * @param deviceId The device id
     * @return The last quota list
     */
    List<QuotaInfoDTO> getLastQuotaList(String deviceId);


//    /**
//     * 获取数值型指标列表
//     * @param page
//     * @param pageSize
//     * @return
//     */
//    IPage<QuotaEntity> queryNumberQuota(Long page,Long pageSize);


}
