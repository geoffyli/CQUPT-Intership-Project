package com.yikekong.controller;

import com.yikekong.dto.DeviceDTO;
import com.yikekong.service.DeviceService;
import com.yikekong.service.NoticeService;
import com.yikekong.vo.DeviceListVO;
import com.yikekong.vo.DeviceQuotaVO;
import com.yikekong.vo.DeviceVO;
import com.yikekong.vo.Pager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/device")
@Slf4j
public class DeviceController {

    @Autowired
    private DeviceService deviceService;


    /**
     * Set the device status
     *
     * @param deviceVO The device VO
     * @return Whether the operation is successful
     */
    @PutMapping("/status")
    public boolean setStatus(@RequestBody DeviceVO deviceVO) {
        return deviceService.setStatus(deviceVO.getSn(), deviceVO.getStatus());
    }


    /**
     * Set device tags
     *
     * @param deviceVO The device VO
     * @return Whether the operation is successful
     */
    @PutMapping("/tags")
    public boolean setTags(@RequestBody DeviceVO deviceVO) {

        return deviceService.updateTags(deviceVO.getSn(), deviceVO.getTags());

    }


    /**
     * Query device list by page
     *
     * @param page     Page number
     * @param pageSize Page size
     * @param sn       Device id
     * @param tag      Device tag
     * @return Device list
     */
    @GetMapping
    public Pager<DeviceDTO> findPage(@RequestParam(value = "page", required = false, defaultValue = "1") Long page,
                                     @RequestParam(value = "pageSize", required = false, defaultValue = "10") Long pageSize,
                                     @RequestParam(value = "sn", required = false) String sn,
                                     @RequestParam(value = "tag", required = false) String tag) {

        return deviceService.queryPage(page, pageSize, sn, tag, null);
    }

    @Autowired
    private NoticeService noticeService;

    /**
     * Get disconnected device information
     *
     * @param param data from EMQX
     */
    @PostMapping("/clientAction")
    public void clientAction(@RequestBody Map<String, String> param) {
        System.out.println(param);
        // Get client id.
        String deviceId = param.get("clientid");
        if (param.get("action").equals("client_connected")) { //如果是联网
            deviceService.updateOnLine(deviceId, true);
//            noticeService.onlineTransfer(deviceId, true);//联网通知
        }
        if (param.get("action").equals("client_disconnected")) { //如果是断网
            deviceService.updateOnLine(deviceId, false);
//            noticeService.onlineTransfer(deviceId, false);//断网通知
        }
    }


    /**
     * Query device quota data
     *
     * @param page     The current page number
     * @param pageSize The number of records per page
     * @param deviceId The device ID
     * @param tag      The tag of the device
     * @param state    The status of the device
     * @return The device quota data
     */
    @GetMapping("/deviceQuota")
    public Pager<DeviceQuotaVO> queryQuotaData(@RequestParam(value = "page", required = false, defaultValue = "1") Long page,
                                               @RequestParam(value = "pageSize", required = false, defaultValue = "10") Long pageSize,
                                               @RequestParam(value = "deviceId", required = false) String deviceId,
                                               @RequestParam(value = "tag", required = false) String tag,
                                               @RequestParam(value = "state", required = false) Integer state) {


        return deviceService.queryDeviceQuota(page, pageSize, deviceId, tag, state);
    }


}
