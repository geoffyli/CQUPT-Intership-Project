package com.sensonet.controller;

import com.google.common.collect.Lists;
import com.sensonet.dto.HeapPointDTO;
import com.sensonet.dto.TrendPointDTO;
import com.sensonet.es.ESRepository;
import com.sensonet.service.ReportService;
import com.sensonet.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/report")
public class ReportController {
    @Autowired
    private ReportService reportService;

    @Autowired
    private ESRepository esRepository;

    /**
     * Get the device statistics (the number of all devices, normal devices, offline devices and alarm devices)
     *
     * @return the number of normal devices, offline devices and alarm devices
     */
    @GetMapping("/statusCollect")
    public List<PieVO> getStatusCollect() {
        return reportService.getStatusCollect();
    }


    /**
     * Get real-time monitor data (the number of all devices and alarm devices)
     *
     * @return the number of all devices and alarm devices
     */
    @GetMapping("/statistic")
    public MonitorVO getDeviceAndAlarmCount() {
        MonitorVO monitorVO = new MonitorVO();
        monitorVO.setDeviceCount(esRepository.getAllDeviceCount());
        monitorVO.setAlarmCount(esRepository.getAlarmCount());
        return monitorVO;
    }


    /**
     * Get the alarm trend
     *
     * @return the number of alarm devices
     */
    @GetMapping("/trend/{startTime}/{endTime}/{type}")
    public LineVO getQuotaTrendCollect(@PathVariable String startTime, @PathVariable String endTime, @PathVariable Integer type) {

        List<TrendPointDTO> trendPointDTOList = reportService.getAlarmTrend(startTime, endTime, type);

        LineVO lineVO = new LineVO();
        lineVO.setXdata(Lists.newArrayList());
        lineVO.setSeries(Lists.newArrayList());

        trendPointDTOList.forEach(t -> {
            lineVO.getXdata().add(formatTime(t.getTime(), type));
            lineVO.getSeries().add(t.getPointValue().longValue());
        });

        return lineVO;
    }


    /**
     * Format time
     *
     * @param time time
     * @param type 1:minute 2:hour 3:day
     * @return
     */
    private String formatTime(String time, int type) {
        LocalDateTime localDateTime = LocalDateTime.parse(time, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        if (type == 1) {
            return localDateTime.getMinute() + "";
        } else if (type == 2) {
            return localDateTime.getHour() + "";
        } else if (type == 3) {
            return localDateTime.getMonthValue() + "月" + localDateTime.getDayOfMonth() + "日";
        }
        return time;
    }


    /**
     * Get the top 10 alarm devices
     *
     * @param startTime start time
     * @param endTime  end time
     * @return the top 10 alarm devices
     */
    @GetMapping("/top10Alarm/{startTime}/{endTime}")
    public List<HeapPointDTO> getTop10Alarm(@PathVariable String startTime, @PathVariable String endTime) {
        return reportService.getTop10Alarm(startTime, endTime);
    }

    /**
     * Get the number of messages received by the device in the last 24 hours
     */
    @GetMapping("/24HourMessages")
    public String get24HourMessages() {
        return reportService.get24HourMessages();
    }

    @GetMapping("/quotaNumber")
    public String getQuotaNumber() {
        return reportService.getQuotaNumber();
    }




}
