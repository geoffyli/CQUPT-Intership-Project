package com.sensonet.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.sensonet.dto.DeviceDTO;
import com.sensonet.dto.PayloadAnalysisResultDTO;
import com.sensonet.dto.QuotaWithAlarmRecordDTO;
import com.sensonet.dto.QuotaInfoDTO;
import com.sensonet.mapper.entity.QuotaEntity;
import com.sensonet.influxdb.InfluxRepository;
import com.sensonet.mapper.QuotaMapper;
import com.sensonet.service.QuotaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class QuotaServiceImpl extends ServiceImpl<QuotaMapper, QuotaEntity> implements QuotaService {
    @Autowired
    private InfluxRepository influxRepository;

    @Override
    public IPage<QuotaEntity> queryPage(Long page, Long pageSize, String name) {
        Page<QuotaEntity> pageResult = new Page<>(page, pageSize);
        LambdaQueryWrapper<QuotaEntity> wrapper = new LambdaQueryWrapper<>();
        if (!Strings.isNullOrEmpty(name)) {
            wrapper.like(QuotaEntity::getName, name);
        }

        return this.page(pageResult, wrapper);
    }


    @Override
    public List<String> getAllSubject() {
        // Create a query wrapper
        QueryWrapper<QuotaEntity> wrapper = new QueryWrapper<>();
        // Select the subject column
        wrapper.lambda().select(QuotaEntity::getSubject);
        // Return the list of subjects
        return this.list(wrapper).stream().map(QuotaEntity::getSubject).collect(Collectors.toList());
    }

    /**
     * For one payload, we
     * @param topic the MQTT topic
     * @param payloadMap the payload map from the msg
     * @return the device info
     */
    @Override
    public PayloadAnalysisResultDTO analysis(String topic, Map<String, Object> payloadMap) {
        /*
        1. Get the quota configuration from mysql by topic.
        2. Encapsulate device information to DeviceInfoDTO.
        3. Encapsulate quota list to DeviceInfoDTO.
        4. Encapsulate device information and quota list.
         */
        PayloadAnalysisResultDTO payloadAnalysisResultDTO = new PayloadAnalysisResultDTO();

        // 1. Get the quota configuration from mysql by topic.
        List<QuotaEntity> quotaList = baseMapper.selectBySubject(topic);
        if (quotaList.size() == 0) return null; // If there's no quota matched, stop analysis

        // 2. Encapsulate device information. (Get the device id)
        String snKey = quotaList.get(0).getSnKey(); // Get the filed name of the device id
        if (Strings.isNullOrEmpty(snKey)) return null;
        String deviceId = payloadMap.get(snKey).toString(); // Get the device id from the payload
        if (Strings.isNullOrEmpty(deviceId)) return null;
        DeviceDTO deviceDTO = new DeviceDTO();
        deviceDTO.setDeviceId(deviceId); // Encapsulate the device id

        // 3. Encapsulate quota list
        List<QuotaWithAlarmRecordDTO> quotaWithAlarmRecordDTOList = Lists.newArrayList();
        for (QuotaEntity quota : quotaList) {
            String quotaKey = quota.getValueKey(); // Get the filed name for quota in payload
            // If the payload contains the filed for the quota
            if (payloadMap.containsKey(quotaKey)) {
                // Create the quota dto, and copy the quota info from mysql
                QuotaWithAlarmRecordDTO quotaWithAlarmRecordDTO = new QuotaWithAlarmRecordDTO();
                BeanUtils.copyProperties(quota, quotaWithAlarmRecordDTO);
                quotaWithAlarmRecordDTO.setQuotaName(quota.getName());

                /*
                 Set the quotaDTO value and stringValue (from payload info)
                 Two types: 1.Number 2.Non-number (string boolean)
                    1.Number      value: content  stringValue: number str
                    2.Non-number  value: 0        stringValue: content
                 */
                if ("String".equals(quotaWithAlarmRecordDTO.getValueType()) || "Boolean".equals(quotaWithAlarmRecordDTO.getValueType())) {
                    // If the quota is not a number
                    quotaWithAlarmRecordDTO.setStringValue((String) payloadMap.get(quotaKey));
                    quotaWithAlarmRecordDTO.setValue(0d);
                } else {
                    // If the quota is a number
                    // There are 2 types of number: 1.String 2.Number
                    if (payloadMap.get(quotaKey) instanceof String) {
                        // If the number is a string
                        quotaWithAlarmRecordDTO.setValue(Double.valueOf((String) payloadMap.get(quotaKey)));
                        quotaWithAlarmRecordDTO.setStringValue((String) payloadMap.get(quotaKey));
                    } else {
                        // If the number is a number
                        quotaWithAlarmRecordDTO.setValue(Double.valueOf(payloadMap.get(quotaKey) + ""));
                        quotaWithAlarmRecordDTO.setStringValue(quotaWithAlarmRecordDTO.getValue() + "");
                    }

                }
                quotaWithAlarmRecordDTO.setDeviceId(deviceId);
                quotaWithAlarmRecordDTOList.add(quotaWithAlarmRecordDTO);
            }
        }

        // 4. Encapsulate device information and quota list
        payloadAnalysisResultDTO.setDevice(deviceDTO);
        payloadAnalysisResultDTO.setQuotaWithAlarmRecordList(quotaWithAlarmRecordDTOList);
        return payloadAnalysisResultDTO;
    }

    @Override
    public void saveQuotaToInflux(List<QuotaWithAlarmRecordDTO> quotaWithAlarmRecordDTOList) {
        // Transform the quotaDTO to quotaInfo
        for (QuotaWithAlarmRecordDTO quotaWithAlarmRecordDTO : quotaWithAlarmRecordDTOList) {
            // Create the quotaInfo and copy the quotaDTO info
            QuotaInfoDTO quotaInfoDTO = new QuotaInfoDTO();
            BeanUtils.copyProperties(quotaWithAlarmRecordDTO, quotaInfoDTO);
            quotaInfoDTO.setQuotaId(quotaWithAlarmRecordDTO.getId() + "");
            // Save the quotaInfo to influx
            influxRepository.add(quotaInfoDTO);
        }

    }

    @Override
    public List<QuotaInfoDTO> getLastQuotaList(String deviceId) {

        String ql = "select last(value),* from quota where deviceId='" + deviceId + "' group by quotaId";
        return influxRepository.query(ql, QuotaInfoDTO.class);
    }

//    @Override
//    public IPage<QuotaEntity> queryNumberQuota(Long page, Long pageSize) {
//
//        Page<QuotaEntity> pageResult = new Page<>(page, pageSize);
//
//        LambdaQueryWrapper<QuotaEntity> wrapper = new LambdaQueryWrapper<>();
//
//        wrapper.eq(QuotaEntity::getValueType, "Long")
//                .or()
//                .eq(QuotaEntity::getValueType, "Integer")
//                .or()
//                .eq(QuotaEntity::getValueType, "Double");
//
//        return this.page(pageResult, wrapper);
//    }

}
