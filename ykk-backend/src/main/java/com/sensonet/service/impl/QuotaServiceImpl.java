package com.sensonet.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.sensonet.dto.DeviceDTO;
import com.sensonet.dto.DeviceInfoDTO;
import com.sensonet.dto.QuotaDTO;
import com.sensonet.dto.QuotaInfo;
import com.sensonet.entity.QuotaEntity;
import com.sensonet.influx.InfluxRepository;
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
     * @param topic 主题
     * @param payloadMap 报文
     * @return
     */
    @Override
    public DeviceInfoDTO analysis(String topic, Map<String, Object> payloadMap) {
        /*
        1. Get the quota configuration from mysql by topic.
        2. Encapsulate device information.
        3. Encapsulate quota list
        4. Encapsulate device information and quota list
         */
        // 1. Get the quota configuration from mysql by topic.
        List<QuotaEntity> quotaList = baseMapper.selectBySubject(topic);
        if (quotaList.size() == 0) return null;

        // 2. Encapsulate device information. (Get the device id)
        String snKey = quotaList.get(0).getSnKey(); // Get the filed name of the device id
        if (Strings.isNullOrEmpty(snKey)) return null;
        String deviceId = (String) payloadMap.get(snKey); // Get the device id
        if (Strings.isNullOrEmpty(deviceId)) return null;
        DeviceDTO deviceDTO = new DeviceDTO();
        deviceDTO.setDeviceId(deviceId);

        // 3. Encapsulate quota list
        List<QuotaDTO> quotaDTOList = Lists.newArrayList();
        for (QuotaEntity quota : quotaList) {
            String quotaKey = quota.getValueKey(); // the filed name in payload that is related to the quota
            // If the payload contains the filed that satisfies the quota
            if (payloadMap.containsKey(quotaKey)) {
                // Create the quota, and copy the quota info from mysql
                QuotaDTO quotaDTO = new QuotaDTO();
                BeanUtils.copyProperties(quota, quotaDTO);
                quotaDTO.setQuotaName(quota.getName());

                /*
                 Set the quotaDTO value and stringValue (from payload info)
                 Two types: 1.Number 2.Non-number (string boolean)
                    1.Number      value: content  stringValue: number str
                    2.Non-number  value: 0        stringValue: content
                 */
                if ("String".equals(quotaDTO.getValueType()) || "Boolean".equals(quotaDTO.getValueType())) {
                    // If the quota is not a number
                    quotaDTO.setStringValue((String) payloadMap.get(quotaKey));
                    quotaDTO.setValue(0d);
                } else {
                    // If the quota is a number
                    // There are 2 types of number: 1.String 2.Number
                    if (payloadMap.get(quotaKey) instanceof String) {
                        // If the number is a string
                        quotaDTO.setValue(Double.valueOf((String) payloadMap.get(quotaKey)));
                        quotaDTO.setStringValue((String) payloadMap.get(quotaKey));
                    } else {
                        // If the number is a number
                        quotaDTO.setValue(Double.valueOf(payloadMap.get(quotaKey) + ""));
                        quotaDTO.setStringValue(quotaDTO.getValue() + "");
                    }
                    quotaDTO.setDeviceId(deviceId);

                }
                quotaDTOList.add(quotaDTO);
            }
        }

        // 4. Encapsulate device information and quota list
        DeviceInfoDTO deviceInfoDTO = new DeviceInfoDTO();
        deviceInfoDTO.setDevice(deviceDTO);
        deviceInfoDTO.setQuotaList(quotaDTOList);

        return deviceInfoDTO;
    }

    @Override
    public void saveQuotaToInflux(List<QuotaDTO> quotaDTOList) {
        // Transform the quotaDTO to quotaInfo
        for (QuotaDTO quotaDTO : quotaDTOList) {
            // Create the quotaInfo and copy the quotaDTO info
            QuotaInfo quotaInfo = new QuotaInfo();
            BeanUtils.copyProperties(quotaDTO, quotaInfo);
            quotaInfo.setQuotaId(quotaDTO.getId() + "");
            // Save the quotaInfo to influx
            influxRepository.add(quotaInfo);
        }

    }

    @Override
    public List<QuotaInfo> getLastQuotaList(String deviceId) {

        String ql = "select last(value),* from quota where deviceId='" + deviceId + "' group by quotaId";
        return influxRepository.query(ql, QuotaInfo.class);
    }

    @Override
    public IPage<QuotaEntity> queryNumberQuota(Long page, Long pageSize) {

        Page<QuotaEntity> pageResult = new Page<>(page, pageSize);

        LambdaQueryWrapper<QuotaEntity> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(QuotaEntity::getValueType, "Long")
                .or()
                .eq(QuotaEntity::getValueType, "Integer")
                .or()
                .eq(QuotaEntity::getValueType, "Double");

        return this.page(pageResult, wrapper);
    }

}
