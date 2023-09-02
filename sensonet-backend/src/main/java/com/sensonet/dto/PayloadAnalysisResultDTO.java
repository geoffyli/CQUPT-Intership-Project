package com.sensonet.dto;

import lombok.Data;

import java.util.List;

/**
 * This DTO is used to store the information of a device.
 */
@Data
public class PayloadAnalysisResultDTO {


    private DeviceDTO device; // device information

    private List<QuotaWithAlarmRecordDTO> quotaWithAlarmRecordList; // quota list


}
