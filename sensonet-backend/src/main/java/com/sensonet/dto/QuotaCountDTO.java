package com.sensonet.dto;

import lombok.Data;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

/**
 * Entity class for the quota count
 */
@Data
@Measurement(name = "quota")
public class QuotaCountDTO {

    @Column(name = "count")
    private Long count;

}
