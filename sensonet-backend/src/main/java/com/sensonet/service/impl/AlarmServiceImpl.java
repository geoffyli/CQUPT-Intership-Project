package com.sensonet.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.sensonet.dto.*;
import com.sensonet.mapper.entity.AlarmEntity;
import com.sensonet.influxdb.InfluxRepository;
import com.sensonet.mapper.AlarmMapper;
import com.sensonet.service.AlarmService;
import com.sensonet.vo.Pager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Service
public class AlarmServiceImpl extends ServiceImpl<AlarmMapper, AlarmEntity> implements AlarmService {


    @Override
    public IPage<AlarmEntity> queryPage(Long page, Long pageSize, String alarmName, Integer quotaId) {
        LambdaQueryWrapper<AlarmEntity> wrapper = new LambdaQueryWrapper<>();
        if (!Strings.isNullOrEmpty(alarmName)) {
            wrapper.like(AlarmEntity::getName, alarmName);
        }
        if (quotaId != null) {
            wrapper.eq(AlarmEntity::getQuotaId, quotaId);
        }
        wrapper.orderByDesc(AlarmEntity::getId);

        //wrapper.orderByDesc(AlarmEntity::getCreateTime);

        Page<AlarmEntity> pageResult = new Page<>(page, pageSize);

        return this.page(pageResult, wrapper);
    }

    /*
     **
     * 获取某一指标下的所有告警设置
     * @param quotaId 指标Id
     * @return
     *
    List<AlarmEntity> getByQuotaId(Integer quotaId);
     */

    private List<AlarmEntity> getByQuotaId(Integer quotaId) {
        QueryWrapper<AlarmEntity> wrapper = new QueryWrapper<>();
        wrapper
                .lambda() // Use lambda expression
                .eq(AlarmEntity::getQuotaId, quotaId) // Filter by quota id
                .orderByDesc(AlarmEntity::getLevel); // Sort by level

        return this.list(wrapper); // Return the list of alarm rules
    }

    @Override
    public AlarmEntity verifyQuota(QuotaDTO quotaDTO) {
        /*
        1. Get alarm rules list by quota id
        2. Traverse the alarm rules list

         */
        // 1. Get alarm rules list by quota id
        List<AlarmEntity> alarmEntityList = getByQuotaId(quotaDTO.getId());
        AlarmEntity alarm = null;
        // 2. Traverse the alarm rules list
        for (AlarmEntity alarmEntity : alarmEntityList) {
            // If the quota value type is string or boolean
            if ("String".equals(quotaDTO.getValueType()) || "Boolean".equals(quotaDTO.getValueType())) {
                if (alarmEntity.getOperator().equals("=")) {
                    if (alarmEntity.getThreshold().toString().equals(quotaDTO.getStringValue())) {
                        alarm = alarmEntity;
                        break;
                    }
                }
            } else // If the quota value type is number
            {
                if (alarmEntity.getOperator().equals(">") && quotaDTO.getValue() > alarmEntity.getThreshold()) {
                    alarm = alarmEntity;
                    break;
                } else if (alarmEntity.getOperator().equals("<") && quotaDTO.getValue() < alarmEntity.getThreshold()) {
                    alarm = alarmEntity;
                    break;
                } else if (alarmEntity.getOperator().equals("=") && quotaDTO.getValue().intValue() == alarmEntity.getThreshold()) {
                    alarm = alarmEntity;
                    break;
                }
            }
        }

        return alarm;
    }

    @Override
    public DeviceInfoDTO verifyDeviceInfo(DeviceInfoDTO deviceInfoDTO) {
        // Get the device
        DeviceDTO deviceDTO = deviceInfoDTO.getDevice();
        // Suppose the device is normal
        deviceDTO.setLevel(0);
        deviceDTO.setAlarm(false);
        deviceDTO.setAlarmName("Normal");
        deviceDTO.setStatus(true);
        deviceDTO.setOnline(true);
        // Traverse the quota list
        for (QuotaDTO quotaDTO : deviceInfoDTO.getQuotaList()) {
            AlarmEntity alarmEntity = verifyQuota(quotaDTO); // Verify the quota
            if (alarmEntity != null) {
                // If the quota is in the alarm, set the quota alarm information
                quotaDTO.setAlarm("1"); // In alarm
                quotaDTO.setAlarmName(alarmEntity.getName()); // Set the alarm name
                quotaDTO.setLevel(alarmEntity.getLevel() + ""); // Set the alarm level
                quotaDTO.setAlarmWebHook(alarmEntity.getWebHook()); // Set the alarm webhook
                quotaDTO.setCycle(alarmEntity.getCycle()); // Set the alarm cycle

                if (alarmEntity.getLevel() > deviceDTO.getLevel()) {

                    deviceDTO.setLevel(alarmEntity.getLevel());
                    deviceDTO.setAlarm(true);
                    deviceDTO.setAlarmName(alarmEntity.getName());
                }

            } else {
                // If the quota is not in the alarm, set the quota alarm information
                quotaDTO.setAlarm("0");
                quotaDTO.setAlarmName("Normal");
                quotaDTO.setLevel("0");
                quotaDTO.setAlarmWebHook("");
                quotaDTO.setCycle(0);
            }
        }
        return deviceInfoDTO;
    }

    @Autowired
    private InfluxRepository influxRepository;

    @Override
    public Pager<QuotaWithTimeDTO> queryAlarmLog(Long page, Long pageSize, String start, String end, String
            alarmName, String deviceId) {
        /*
        Construct "where" clause
         */
        StringBuilder whereQl = new StringBuilder("where alarm='1' "); // alarm='1' means in alarm
        if (!Strings.isNullOrEmpty(start)) {
            whereQl.append("and time>='").append(start).append("' "); // Filter by start time
        }
        if (!Strings.isNullOrEmpty(end)) {
            whereQl.append("and time<='").append(end).append("' "); // Filter by end time
        }
        if (!Strings.isNullOrEmpty(alarmName)) {
            whereQl.append("and alarmName=~/").append(alarmName).append("/ "); // Filter by alarm name (vague match)
        }
        if (!Strings.isNullOrEmpty(deviceId)) {
            whereQl.append("and deviceId=~/^").append(deviceId).append("/ "); // Filter by device id (prefix match)
        }

        /*
        Construct query language
         */
        StringBuilder listQl = new StringBuilder("select * from quota  ");
        listQl.append(whereQl);
        listQl.append("order by desc limit ").append(pageSize).append(" offset ").append((page - 1) * pageSize);


        /*
        Construct count query language
         */
        StringBuilder countQl = new StringBuilder("select count(value) from quota ");
        countQl.append(whereQl);


        /*
        Execute query language
         */
        List<QuotaWithTimeDTO> quotaList = influxRepository.query(listQl.toString(), QuotaWithTimeDTO.class);

        /*
        Convert time format
         */
        for (QuotaWithTimeDTO quotaAllInfo : quotaList) {
            //2020-09-19T09:58:34.926Z   DateTimeFormatter.ISO_OFFSET_DATE_TIME
            //2020-09-19 09:58:34
            LocalDateTime dateTime = LocalDateTime.parse(quotaAllInfo.getTime(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            String time = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"));
            quotaAllInfo.setTime(time);
        }

        /*
        Execute count query language
         */
        List<QuotaCountDTO> quotaCountDTO = influxRepository.query(countQl.toString(), QuotaCountDTO.class);

        /*
        Return the result
         */
        if (quotaCountDTO == null || quotaCountDTO.size() == 0) {
            // Set an empty pager
            Pager<QuotaWithTimeDTO> pager = new Pager<QuotaWithTimeDTO>(0L, 0L);
            pager.setPage(0);
            pager.setItems(Lists.newArrayList());
            return pager;
        }

        Long totalCount = quotaCountDTO.get(0).getCount(); // Get the total count
        // Construct the pager
        Pager<QuotaWithTimeDTO> pager = new Pager<>(totalCount, pageSize);
        pager.setPage(page);
        pager.setItems(quotaList);

        return pager;
    }


}
