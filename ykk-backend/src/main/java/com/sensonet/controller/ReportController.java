package com.sensonet.controller;

import com.google.common.collect.Lists;
import com.sensonet.dto.HeapPoint;
import com.sensonet.dto.TrendPoint;
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
     * Get the device statistics
     * @return the number of normal devices, offline devices and alarm devices
     */
    @GetMapping("/statusCollect")
    public List<PieVO> getStatusCollect(){
        return  reportService.getStatusCollect();
    }


    /**
     * Get real-time monitor data
     * @return the number of all devices and alarm devices
     */
    @GetMapping("/statistic")
    public MonitorVO getMonitorData(){
        MonitorVO monitorVO=new MonitorVO();
        monitorVO.setDeviceCount( esRepository.getAllDeviceCount() );
        monitorVO.setAlarmCount( esRepository.getAlarmCount());
        return monitorVO;
    }


    /**
     * 获取告警趋势
     * @return
     */
    @GetMapping("/trend/{startTime}/{endTime}/{type}")
    public LineVO getQuotaTrendCollect(@PathVariable String startTime,@PathVariable String endTime,@PathVariable Integer type){

        List<TrendPoint> trendPointList = reportService.getAlarmTrend(startTime, endTime, type);

        LineVO lineVO=new LineVO();
        lineVO.setXdata(Lists.newArrayList());
        lineVO.setSeries(Lists.newArrayList());

        trendPointList.forEach( t->{
            lineVO.getXdata().add( formatTime(t.getTime(),type) );
            lineVO.getSeries().add( t.getPointValue().longValue());
        });

        return lineVO;
    }


    /**
     * 格式化日期串
     * @param time
     * @param type  1：分钟  2：小时  3：天
     * @return
     */
    private String formatTime(String time,int type){
        LocalDateTime localDateTime=LocalDateTime.parse(time, DateTimeFormatter.ISO_OFFSET_DATE_TIME );
        if(type==1){
            return  localDateTime.getMinute()+"";
        }
        if(type==2){
            return    localDateTime.getHour()+"";
        }
        if(type==3){
            return localDateTime.getMonthValue()+"月"+localDateTime.getDayOfMonth()+"日";
        }
        return time;
    }


    /**
     * 异常top10
     * @param startTime
     * @param endTime
     * @return
     */
    @GetMapping("/top10Alarm/{startTime}/{endTime}")
    public List<HeapPoint> getTop10Alarm(@PathVariable String startTime, @PathVariable String endTime ){
        return reportService.getTop10Alarm(startTime,endTime);
    }


    /**
     * 通过指标查询设备列表
     * @param page
     * @param pageSize
     * @param quotaId
     * @return
     */
    @GetMapping("/devices")
    public Pager<String> getDeviceByQuota(
            @RequestParam(value = "page",required = false,defaultValue = "1") Long page,
            @RequestParam(value = "pageSize",required = false,defaultValue = "10") Long pageSize,
            @RequestParam(value = "quotaId") String quotaId ){

        return  reportService.getDeviceByQuota( page,pageSize,quotaId );

    }

    /**
     * 报表预览
     * @param previewVO
     * @return
     */
    @PostMapping("/preview")
    public BoardQuotaVO getPreviewData( @RequestBody PreviewVO previewVO ){
        BoardQuotaVO boardData = reportService.getBoardData(
                previewVO.getQuotaId(), previewVO.getDeviceIdList(), previewVO.getStart(), previewVO.getEnd(), previewVO.getType());

        //时间处理
        List<String> xdata=Lists.newArrayList();

        for(String x:boardData.getXdata()){
            xdata.add(formatTime(x,previewVO.getType() ))  ;
        }
        boardData.setXdata(xdata);

        return boardData;
    }


    /**
     * 获取面板数据
     * @param id
     * @param start
     * @param end
     * @param type
     * @return
     */
    @GetMapping("/boardData/{id}/{start}/{end}/{type}")
    public BoardQuotaVO getBoardData(@PathVariable Integer id,
                                     @PathVariable String start,
                                     @PathVariable String end,
                                     @PathVariable Integer type){
        return reportService.getBoardData(id,start,end,type);
    }

}
