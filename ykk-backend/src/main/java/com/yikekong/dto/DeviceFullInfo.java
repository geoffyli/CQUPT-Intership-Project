package com.yikekong.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DeviceFullInfo implements Serializable {


    private String deviceId;
    private String location;
    private Boolean online;
    private Boolean alarm;
    private List<QuotaInfo> quotaList;

}
