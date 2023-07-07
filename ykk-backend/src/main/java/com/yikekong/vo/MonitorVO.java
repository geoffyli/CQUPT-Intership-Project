package com.yikekong.vo;

import lombok.Data;

/**
 * 监控数据
 */
@Data
public class MonitorVO {

    private Long deviceCount;//设备数量

    private Long alarmCount;// 告警设备数

}
