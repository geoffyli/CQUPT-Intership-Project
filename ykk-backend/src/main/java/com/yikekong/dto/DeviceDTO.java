package com.yikekong.dto;

import lombok.Data;

import java.io.Serializable;

/**
    * This class is used to represent a device.
 */
@Data
public class DeviceDTO implements Serializable {

    private String deviceId; // The device ID

    private Boolean alarm; // Whether the device is in alarm

    private String alarmName; // The name of the alarm

    private Integer level; // The level of the alarm

    private Boolean online; // Whether the device is online

    private String tag; // The tag of the device

    private Boolean status; // The status of the device

}
