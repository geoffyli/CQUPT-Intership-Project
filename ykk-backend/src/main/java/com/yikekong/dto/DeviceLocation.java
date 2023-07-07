package com.yikekong.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class DeviceLocation implements Serializable {

    private String deviceId;//设备编号

    private String location;//经纬度 用“，”分割，纬度在前


}
