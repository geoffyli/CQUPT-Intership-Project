package com.yikekong.service.impl;

import com.google.common.collect.Lists;
import com.yikekong.dto.*;
import com.yikekong.entity.BoardEntity;
import com.yikekong.es.ESRepository;
import com.yikekong.influx.InfluxRepository;
import com.yikekong.service.BoardService;
import com.yikekong.service.ReportService;
import com.yikekong.vo.BoardQuotaData;
import com.yikekong.vo.BoardQuotaVO;
import com.yikekong.vo.Pager;
import com.yikekong.vo.PieVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {


    @Autowired
    private ESRepository esRepository;

    @Override
    public List<PieVO> getStatusCollect() {

        Long allDeviceCount = esRepository.getAllDeviceCount();//全部设备数量

        Long offlineCount = esRepository.getOfflineCount();//离线设备数量

        Long alarmCount = esRepository.getAlarmCount();//报警设备数量


        PieVO devicePie=new PieVO();
        devicePie.setName("正常运转");
        devicePie.setValue(allDeviceCount-offlineCount-alarmCount);

        PieVO offlinePie=new PieVO();
        offlinePie.setName("离线");
        offlinePie.setValue(offlineCount);

        PieVO alarmPie =new PieVO();
        alarmPie.setName("报警");
        alarmPie.setValue(alarmCount);

        List<PieVO> pieVOList= Lists.newArrayList();
        pieVOList.add(devicePie);
        pieVOList.add(offlinePie);
        pieVOList.add(alarmPie);

        return pieVOList;
    }

    @Autowired
    private InfluxRepository influxRepository;

    @Override
    public List<TrendPoint> getAlarmTrend(String start, String end, int type) {

        StringBuilder ql=new StringBuilder("select count(value) as pointValue from quota where alarm='1' ");
        ql.append("and time>='"+ start +"' and time<='"+ end+"' ");

        if(type==1){
            ql.append("group by time(1m)");
        }
        if(type==2){
            ql.append("group by time(1h)");
        }
        if(type==3){
            ql.append("group by time(1d)");
        }

        List<TrendPoint> trendPointList = influxRepository.query(ql.toString(), TrendPoint.class);

        return trendPointList;
    }

    @Override
    public List<HeapPoint> getTop10Alarm(String startTime, String endTime) {

        StringBuilder ql=new StringBuilder("select top(heapValue,deviceId,quotaId,quotaName,10 )  as heapValue ");
        ql.append("from (select count(value) as heapValue from quota where alarm='1' ");
        ql.append("and time>='"+ startTime +"' and time<='"+ endTime +"' ");
        ql.append("group by deviceId,quotaId,quotaName  ) order by desc");

        List<HeapPoint> heapPointList = influxRepository.query(ql.toString(), HeapPoint.class);

        return heapPointList;
    }

    @Override
    public Pager<String> getDeviceByQuota(Long page, Long pageSize, String quotaId) {

        String fromQl=" from ( select deviceId,value from quota where quotaId='"+ quotaId+"' group by deviceId,quotaId  ) ";

        String listQl="select distinct(deviceId ) as deviceId "+fromQl+" limit "+pageSize  +" offset "+(page-1)*pageSize;

        String countQl=" select count( distinct(deviceId )) as count "+fromQl;

        List<QuotaInfo> quotaInfoList = influxRepository.query(listQl, QuotaInfo.class);

        //设备id列表
        List<String> deviceIdList = quotaInfoList.stream().map(quotaInfo -> quotaInfo.getDeviceId()).collect(Collectors.toList());

        //统计记录个数
        List<QuotaCount> quotaCountList = influxRepository.query(countQl, QuotaCount.class);

        if( quotaCountList==null || quotaCountList.size()==0 ){
            Pager<String> pager=new Pager<String>(0L,0L);
            pager.setItems(Lists.newArrayList());
            return pager;
        }

        Long count = quotaCountList.get(0).getCount();

        Pager<String> pager=new Pager<String>(count,pageSize);
        pager.setItems(deviceIdList);

        return pager;
    }

    @Override
    public List<TrendPoint2> getQuotaTrend(String startTime, String endTime, String quotaId, String deviceId, int type) {

        StringBuilder ql=new StringBuilder("select first(value) as pointValue from quota ");
        ql.append("where time>='"+ startTime+"' and time<='"+ endTime +"' "  );
        ql.append("and quotaId='"+quotaId +"' ");
        ql.append("and deviceId='"+ deviceId +"' ");

        if(type==1){ //1小时
            ql.append("group by time(1m)");
        }
        if(type==2){ //1天
            ql.append("group by time(1h)");
        }
        if(type==3){ //7天
            ql.append("group by time(1d)");
        }

        List<TrendPoint2> trendPoint2List = influxRepository.query(ql.toString(), TrendPoint2.class);

        return replenish(trendPoint2List);
    }

    @Override
    public BoardQuotaVO getBoardData(String quotaId, List<String> deviceIds, String startTime, String endTime, Integer type) {
        //参数校验
        if( quotaId==null || deviceIds==null || deviceIds.size()==0 ){
            return new BoardQuotaVO();
        }

        BoardQuotaVO boardQuotaVO=new BoardQuotaVO();
        boardQuotaVO.setSeries(  Lists.newArrayList());

        for( String deviceId:deviceIds ){  //循环每个设备
            //每个设备的指标趋势
            List<TrendPoint2> trendPoint2List = getQuotaTrend(startTime, endTime, quotaId, deviceId, type);

            //x轴
            if(boardQuotaVO.getXdata()==null){
                boardQuotaVO.setXdata(  trendPoint2List.stream().map(trendPoint2 -> trendPoint2.getTime()  ).collect( Collectors.toList() ) );
            }

            //数据
            BoardQuotaData boardQuotaData=new BoardQuotaData();
            boardQuotaData.setName( deviceId );
            boardQuotaData.setData( trendPoint2List.stream().map( trendPoint2 -> trendPoint2.getPointValue() ).collect(Collectors.toList())  );

            boardQuotaVO.getSeries().add(boardQuotaData);

        }


        return boardQuotaVO;
    }


    /**
     * 填充数据
     * @param trendPoint2List
     * @return
     */
    private List<TrendPoint2> replenish(List<TrendPoint2> trendPoint2List){

        Double  previousValue=null;// 上一个值
        for(TrendPoint2 trendPoint2: trendPoint2List ){
            if(trendPoint2.getPointValue()!=null){
                previousValue=trendPoint2.getPointValue();
                break;
            }
        }
        if(previousValue==null){
            previousValue=0d;
        }

        //数据填充逻辑
        for( TrendPoint2 trendPoint2: trendPoint2List){
            if(trendPoint2.getPointValue()==null){
                trendPoint2.setPointValue(previousValue);
            }
            previousValue=trendPoint2.getPointValue();
        }
        return trendPoint2List;
    }


    @Autowired
    private BoardService boardService;

    @Override
    public BoardQuotaVO getBoardData(Integer id, String start, String end, Integer type) {
        //从mysql中获取面板实体数据
        BoardEntity boardEntity = boardService.getById(id);
        if(boardEntity == null) return null;

        List<String> deviceIdList = Arrays.asList(boardEntity.getDevice().split(","));
        BoardQuotaVO boardQuotaVO  = getBoardData(boardEntity.getQuota() + "",deviceIdList,start,end,type);
        boardQuotaVO.setName(boardEntity.getName());

        return boardQuotaVO;
    }

}
