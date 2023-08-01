package com.yikekong.service.impl;
import com.google.common.collect.Lists;
import com.yikekong.common.SystemDefinition;
import com.yikekong.dto.DeviceDTO;
import com.yikekong.dto.QuotaInfo;
import com.yikekong.es.ESRepository;
import com.yikekong.service.DeviceService;
import com.yikekong.service.QuotaService;
import com.yikekong.vo.DeviceQuotaVO;
import com.yikekong.vo.Pager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class DeviceServiceImpl implements DeviceService {


    @Autowired
    private ESRepository esRepository;

    @Override
    public boolean setStatus(String deviceId, Boolean status) {
        // Search for the device, determine the switch status, and do not process if it is closed
        DeviceDTO deviceDTO = findDevice(deviceId);
        if (deviceDTO == null) return false;
        boolean res = esRepository.updateStatus(deviceId, status);
        deviceDTO.setStatus(status);
//        refreshDevice(deviceDTO);
        return res;
    }

    @Override
    public boolean updateTags(String deviceId, String tag) {
        // Search for the device, determine the switch status, and do not process if it is closed
        DeviceDTO deviceDTO = findDevice(deviceId);
        if (deviceDTO == null) return false;
        // Update the device tag
        return esRepository.updateDeviceTag(deviceId, tag);
    }

    @Override
    public Pager<DeviceDTO> queryPage(Long page, Long pageSize, String deviceId, String tag, Integer state) {
        return esRepository.searchDevice(page, pageSize, deviceId, tag, state);
    }

    @Override
    public boolean saveDeviceInfo(DeviceDTO deviceDTO) {
        //查询设备 ，判断开关状态 ，如果是关闭则不处理
        DeviceDTO device = findDevice(deviceDTO.getDeviceId());
        if (device != null && !device.getStatus()) return false;

        // 如果当前设备查不到，新增
        if (device == null) {
            esRepository.addDevices(deviceDTO);
        } else {
            //如果可以查询到，更新告警信息
            esRepository.updateDevicesAlarm(deviceDTO);
        }
//        refreshDevice(deviceDTO);
        return true;
    }

    @Override
    public void updateOnLine(String deviceId, Boolean online) {

        if (deviceId.startsWith("webclient") || deviceId.startsWith("monitor")) {
            return;
        }

        DeviceDTO deviceDTO = findDevice(deviceId);
        if (deviceDTO == null) return;
        esRepository.updateOnline(deviceId, online);
        deviceDTO.setOnline(online);
//        refreshDevice(deviceDTO);

    }


    @Autowired
    private QuotaService quotaService;

    @Override
    public Pager<DeviceQuotaVO> queryDeviceQuota(Long page, Long pageSize, String deviceId, String tag, Integer state) {

        //1.查询设备列表

        Pager<DeviceDTO> pager = esRepository.searchDevice(page, pageSize, deviceId, tag, state);


        //2.查询指标列表
        List<DeviceQuotaVO> deviceQuotaVOList = Lists.newArrayList();
        pager.getItems().forEach(deviceDTO -> {
            DeviceQuotaVO deviceQuotaVO = new DeviceQuotaVO();
            BeanUtils.copyProperties(deviceDTO, deviceQuotaVO);
            //查询指标
            List<QuotaInfo> quotaList = quotaService.getLastQuotaList(deviceDTO.getDeviceId());
            deviceQuotaVO.setQuotaList(quotaList);
            deviceQuotaVOList.add(deviceQuotaVO);
        });

        //3.封装返回结果
        Pager<DeviceQuotaVO> pageResult = new Pager(pager.getCounts(), pageSize);
        pageResult.setItems(deviceQuotaVOList);

        return pageResult;
    }

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * Search device by device ID
     *
     * @param deviceId device ID
     * @return device
     */
    private DeviceDTO findDevice(String deviceId) {

        return esRepository.searchDeviceById(deviceId);
    }


//    /**
//     * 刷新缓存
//     * @param deviceDTO
//     */
//    private void refreshDevice(DeviceDTO deviceDTO ){
//        if(deviceDTO==null) return;
//        redisTemplate.boundHashOps(SystemDefinition.DEVICE_KEY).put(deviceDTO.getDeviceId(),deviceDTO);
//    }
//
//
}
