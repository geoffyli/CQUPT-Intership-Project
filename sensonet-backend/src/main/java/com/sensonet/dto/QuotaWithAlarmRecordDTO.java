package com.sensonet.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * This DTO is used to store the information of a quota.
 */
@Data
public class QuotaWithAlarmRecordDTO implements Serializable {

    /*
    Copied from QuotaEntity class
     */

    private Integer id; // primary key

    private String quotaName; // quota name

    private String unit; // unit

    private String subject; // subject

    private String valueKey; // value key

    private String valueType; // value type

    private String snKey; // device key

    private String webhook; // webhook

    private String referenceValue; // reference value


    private Double value; // quota value (number)
    private String stringValue; // quota value (non-number)

    private String deviceId; // device id

    /*
    The following fields are alarm-related.
     */

    private String alarm; // alarm status, "0" for no alarm, "1" for alarm

    private String alarmName; // The name of the alarm

    private String level; // The level of the alarm

    private String alarmWebHook; // The webhook of the alarm

    private Integer cycle; // The cycle of the alarm


}
