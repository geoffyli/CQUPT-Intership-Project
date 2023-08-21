package com.yikekong.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yikekong.dto.DeviceInfoDTO;
import com.yikekong.dto.QuotaAllInfo;
import com.yikekong.dto.QuotaDTO;
import com.yikekong.entity.AlarmEntity;
import com.yikekong.vo.Pager;

import java.util.List;

public interface AlarmService extends IService<AlarmEntity>{
    /**
     * 分页查询告警设置
     * @param page
     * @param pageSize
     * @param alarmName
     * @param quotaId
     * @return
     */
    IPage<AlarmEntity> queryPage(Long page,Long pageSize,String alarmName,Integer quotaId);


    /**
     * View the alarm information of a device
     * @param quotaDTO QuotaDTO
     * @return AlarmEntity object or null if no alarm is triggered
     */
    AlarmEntity verifyQuota(QuotaDTO quotaDTO);


    /**
     * Encapsulate the alarm information of a device
     * @param deviceInfoDTO DeviceInfoDTO
     * @return DeviceInfoDTO object
     */
    DeviceInfoDTO verifyDeviceInfo(DeviceInfoDTO deviceInfoDTO);


    /**
     * Query the alarm log
     * @param page The page number
     * @param pageSize The page size
     * @param start The start time
     * @param end The end time
     * @param alarmName The alarm name
     * @param deviceId The device id
     * @return The alarm log in pager
     */
    Pager<QuotaAllInfo> queryAlarmLog( Long page,Long pageSize,String start,String end,String alarmName,String deviceId );

}