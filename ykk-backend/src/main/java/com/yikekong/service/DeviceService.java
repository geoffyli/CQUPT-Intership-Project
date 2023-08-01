package com.yikekong.service;


import com.yikekong.dto.DeviceDTO;
import com.yikekong.vo.DeviceQuotaVO;
import com.yikekong.vo.Pager;

public interface DeviceService {

    /**
     * Update the device status
     *
     * @param deviceId The device ID
     * @param status   The status of the device
     * @return true if the status is updated successfully, otherwise false
     */
    boolean setStatus(String deviceId, Boolean status);


    /**
     * Update the device tag
     *
     * @param deviceId The device ID
     * @param tag      The tag of the device
     * @return true if the tag is updated successfully, otherwise false
     */
    boolean updateTags(String deviceId, String tag);


    /**
     * Search for devices by page
     *
     * @param page     The current page number
     * @param pageSize The number of records per page
     * @param deviceId The device ID
     * @param tag      The tag of the device
     * @param state    The status of the device
     * @return The device information
     */
    Pager<DeviceDTO> queryPage(Long page, Long pageSize, String deviceId, String tag, Integer state);

    /**
     * 存储设备信息
     *
     * @param deviceDTO
     * @return
     */
    boolean saveDeviceInfo(DeviceDTO deviceDTO);


    /**
     * 更新在线状态
     *
     * @param deviceId
     * @param online
     */
    void updateOnLine(String deviceId, Boolean online);


    /**
     * 查询设备详情
     *
     * @param page
     * @param pageSize
     * @param deviceId
     * @param tag
     * @param state
     * @return
     */
    Pager<DeviceQuotaVO> queryDeviceQuota(Long page, Long pageSize, String deviceId, String tag, Integer state);

}
