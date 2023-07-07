package com.yikekong.dto;

import lombok.Data;

import java.io.Serializable;
@Data
public class AlarmMsg implements Serializable {

    private String deviceId;//设备id

    private String alarmName;//告警名称

    private String quotaName;//指标名称

    private String stringValue;//文本指标值

    private Double valuel;//指标值

    private Integer level;// 告警级别

    private Boolean online;//联网状态

    private Boolean alarm;// 是否告警


}
