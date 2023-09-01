package com.sensonet.dto;

import lombok.Data;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

/**
 * This class is used to store the quota information in influxDB with time property.
 */
@Data
@Measurement(name = "quota")
public class QuotaWithTimeDTO extends QuotaInfoDTO {

    @Column(name = "time")
    private String time;

}
