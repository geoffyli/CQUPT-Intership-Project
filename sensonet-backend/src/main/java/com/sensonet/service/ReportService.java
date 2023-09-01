package com.sensonet.service;

import com.sensonet.dto.HeapPointDTO;
import com.sensonet.dto.TrendPointDTO;
//import com.sensonet.dto.TrendPoint2;
//import com.sensonet.vo.BoardQuotaVO;
import com.sensonet.vo.PieVO;

import java.util.List;

/**
 * Report data to the front end
 */
public interface ReportService {


    /**
     * Get the device status statistics
     * @return
     */
    List<PieVO> getStatusCollect();


    /**
     * Get the alarm trend
     * @param start the start time
     * @param end the end time
     * @param type 1: min, 2: hour, 3: day
     * @return the number of alarm devices
     */
    List<TrendPointDTO> getAlarmTrend(String start, String end, int type);


    /**
     * Get the device that has the most alarms
     * @param startTime the start time
     * @param endTime the end time
     * @return the device that has the most alarms
     */
    List<HeapPointDTO> getTop10Alarm(String startTime, String endTime );

    /**
     * Get the number of messages in the last 24 hours
     * @return the number of messages in the last 24 hours
     */
    String get24HourMessages();

    /**
     * Get the number of quotas
     * @return the number of quotas
     */
    String getQuotaNumber();

}
