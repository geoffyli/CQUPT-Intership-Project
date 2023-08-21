package com.yikekong.vo;

import com.yikekong.dto.QuotaInfo;
import lombok.Data;

import java.util.List;

/**
 * Device quota info VO
 */
@Data
public class DeviceQuotaVO {

    private String deviceId; // The device id

    private Boolean online; // Whether the device is online

    private Integer level; // The alarm level

    private List<QuotaInfo> quotaList; // The quota list
        
}
