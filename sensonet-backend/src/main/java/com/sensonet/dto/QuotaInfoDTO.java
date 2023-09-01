package com.sensonet.dto;

import lombok.Data;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

/**
 * This class is used to store the quota information in influxDB.
 */
@Data
@Measurement(name = "quota")
public class QuotaInfoDTO {

    @Column(name = "deviceId", tag = true)
    private String deviceId; // The device ID

    @Column(name = "quotaId", tag = true)
    private String quotaId; // The quota ID

    @Column(name = "quotaName", tag = true)
    private String quotaName; // The quota name

    @Column(name = "alarm", tag = true)
    private String alarm; // The alarm status, 0 means no alarm, 1 means alarm

    @Column(name = "level", tag = true)
    private String level; // The alarm level

    @Column(name = "alarmName", tag = true)
    private String alarmName; // The alarm name

    @Column(name = "unit", tag = true)
    private String unit; // The unit of the quota

    @Column(name = "referenceValue", tag = true)
    private String referenceValue; // The reference value of the quota

    @Column(name = "value")
    private Double value; // Number quota

    @Column(name = "stringValue")
    private String stringValue; // Non-number quota

}
