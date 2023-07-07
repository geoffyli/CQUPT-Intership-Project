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
     * 获取某一指标下的所有告警设置
     * @param quotaId 指标Id
     * @return
     */
    List<AlarmEntity> getByQuotaId(Integer quotaId);


    /**
     * 根据指标判断告警信息
     * @param quotaDTO
     * @return
     */
    AlarmEntity verifyQuota(QuotaDTO quotaDTO);


    /**
     * 告警信息封装
     * @param deviceInfoDTO
     * @return
     */
    DeviceInfoDTO verifyDeviceInfo(DeviceInfoDTO deviceInfoDTO);


    /**
     * 告警日志查询
     * @param page 页码
     * @param pageSize 页大小
     * @param start 开始时间
     * @param end 截至时间
     * @param alarmName 告警名称
     * @param deviceId 设备编号
     * @return
     */
    Pager<QuotaAllInfo> queryAlarmLog( Long page,Long pageSize,String start,String end,String alarmName,String deviceId );

}