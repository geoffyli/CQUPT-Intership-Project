package com.yikekong.dto;

import lombok.Data;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

/**
 * 包含time的指标对象
 */
@Data
@Measurement(name = "quota")
public class QuotaAllInfo extends QuotaInfo{

    @Column(name = "time")
    private String time;

}
