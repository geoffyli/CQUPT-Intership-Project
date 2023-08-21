package com.sensonet.service;


import com.sensonet.dto.DeviceDTO;
import com.sensonet.vo.DeviceQuotaVO;
import com.sensonet.vo.Pager;

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
     * Save device information
     *
     * @param deviceDTO
     * @return
     */
    boolean saveDeviceInfo(DeviceDTO deviceDTO);


    /**
     * Update the device status
     *
     * @param deviceId The device ID
     * @param online   The status of the device
     */
    void updateOnLine(String deviceId, Boolean online);


    /**
     * Query device quota details
     *
     * @param page     The current page number
     * @param pageSize The number of records per page
     * @param deviceId The device ID
     * @param tag      The tag of the device
     * @param state    The status of the device
     * @return The device quota information
     */
    Pager<DeviceQuotaVO> queryDeviceQuota(Long page, Long pageSize, String deviceId, String tag, Integer state);

}
