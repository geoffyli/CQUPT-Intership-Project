package com.sensonet.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.sensonet.dto.*;
import com.sensonet.es.ESRepository;
import com.sensonet.influxdb.InfluxRepository;
import com.sensonet.mapper.QuotaMapper;
import com.sensonet.mapper.entity.QuotaEntity;
import com.sensonet.service.ReportService;
import com.sensonet.vo.PieVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    private final QuotaMapper quotaMapper;
    @Autowired
    public ReportServiceImpl(QuotaMapper quotaMapper) {
        this.quotaMapper = quotaMapper;
    }

    public Integer getQuotaCountService() {
        return quotaMapper.getQuotaCount();
    }


    @Autowired
    private ESRepository esRepository;

    @Override
    public List<PieVO> getStatusCollect() {
        /*
        Get the number of all devices, offline devices and alarm devices
         */
        Long allDeviceCount = esRepository.getAllDeviceCount();
        Long offlineCount = esRepository.getOfflineCount();
        Long alarmCount = esRepository.getAlarmCount();


        PieVO devicePie = new PieVO();
        devicePie.setName("Normal");
        devicePie.setValue(allDeviceCount - offlineCount - alarmCount);

        PieVO offlinePie = new PieVO();
        offlinePie.setName("Offline");
        offlinePie.setValue(offlineCount);

        PieVO alarmPie = new PieVO();
        alarmPie.setName("Alarm");
        alarmPie.setValue(alarmCount);

        /*
        Put the data into the list
         */
        List<PieVO> pieVOList = Lists.newArrayList();
        pieVOList.add(devicePie);
        pieVOList.add(offlinePie);
        pieVOList.add(alarmPie);

        return pieVOList;
    }

    @Autowired
    private InfluxRepository influxRepository;

    @Override
    public List<TrendPointDTO> getAlarmTrend(String start, String end, int type) {

        StringBuilder ql = new StringBuilder("select count(value) as pointValue from quota where alarm='1' ");
        ql.append("and time>='").append(start).append("' and time<='").append(end).append("' ");

        if (type == 1) {
            ql.append("group by time(1m)");
        }
        if (type == 2) {
            ql.append("group by time(1h)");
        }
        if (type == 3) {
            ql.append("group by time(1d)");
        }

        List<TrendPointDTO> trendPointDTOList = influxRepository.query(ql.toString(), TrendPointDTO.class);

        return trendPointDTOList;
    }

    @Override
    public List<HeapPointDTO> getTop10Alarm(String startTime, String endTime) {

        String ql = "select top(heapValue,deviceId,quotaId,quotaName,10 )  as heapValue " + "from (select count(value) as heapValue from quota where alarm='1' " +
                "and time>='" + startTime + "' and time<='" + endTime + "' " +
                "group by deviceId,quotaId,quotaName  ) order by desc";

        return influxRepository.query(ql, HeapPointDTO.class);
    }

    @Override
    public String get24HourMessages() {
        String ql = "SELECT count(*) FROM quota WHERE time >= now() - 24h";
        return influxRepository.queryNum(ql);
    }

    @Override
    public String getQuotaNumber() {
        return getQuotaCountService().toString();
    }
}
