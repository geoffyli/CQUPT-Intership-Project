package com.sensonet.dto;

import lombok.Data;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

import java.io.Serializable;

/**
 * This class is used to store the data of the device trend chart
 */
@Data
@Measurement(name = "quota")
public class TrendPointDTO implements Serializable {

    @Column(name = "time")
    private String time;//时间


    @Column(name = "pointValue")
    private Integer pointValue;//时间点数据

}
