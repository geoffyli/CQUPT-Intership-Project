package com.yikekong.service;


import com.yikekong.dto.DeviceDTO;
import com.yikekong.vo.DeviceQuotaVO;
import com.yikekong.vo.Pager;

public interface DeviceService  {

    /**
     * 更改设备状态
     * @param deviceId
     * @param status
     * @return
     */
    boolean setStatus(String deviceId,Boolean status);


    /**
     * 更改设备标签
     * @param deviceId
     * @param tags
     * @return
     */
    boolean updateTags(String deviceId,String tags);


    /**
     * 搜索设备
     * @param page
     * @param pageSize
     * @param deviceId
     * @param tags
     * @param state
     * @return
     */
    Pager<DeviceDTO> queryPage(Long page,Long pageSize,String deviceId,String tags,Integer state );

    /**
     * 存储设备信息
     * @param deviceDTO
     * @return
     */
    boolean saveDeviceInfo(DeviceDTO deviceDTO);


    /**
     * 更新在线状态
     * @param deviceId
     * @param online
     */
    void updateOnLine(String deviceId,Boolean online);


    /**
     * 查询设备详情
     * @param page
     * @param pageSize
     * @param deviceId
     * @param tag
     * @param state
     * @return
     */
    Pager<DeviceQuotaVO> queryDeviceQuota( Long page,Long pageSize,String deviceId,String tag,Integer state );

}
