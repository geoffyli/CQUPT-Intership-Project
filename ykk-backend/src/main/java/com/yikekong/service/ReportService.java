package com.yikekong.service;

import com.yikekong.dto.HeapPoint;
import com.yikekong.dto.TrendPoint;
import com.yikekong.dto.TrendPoint2;
import com.yikekong.vo.BoardQuotaVO;
import com.yikekong.vo.Pager;
import com.yikekong.vo.PieVO;

import java.util.List;

/**
 * 报表服务
 */
public interface ReportService {


    /**
     * 设备状态分布
     * @return
     */
    List<PieVO> getStatusCollect();


    /**
     * 获取异常趋势指标
     * @param start 开始时间
     * @param end 结束时间
     * @param type 时间统计类型（1：1小时  2：1天  3：7天  ）
     * @return
     */
    List<TrendPoint> getAlarmTrend(String start,String end,int type);


    /**
     * 获取一定时间范围内的报警次数最多的设备指标
     * @param startTime
     * @param endTime
     * @return
     */
    List<HeapPoint> getTop10Alarm( String startTime,String endTime );


    /**
     * 通过指标获取设备列表
     * @param page
     * @param pageSize
     * @param quotaId
     * @return
     */
    Pager<String> getDeviceByQuota(Long page,Long pageSize,String quotaId);


    /**
     * 获取指标趋势
     * @param startTime
     * @param endTime
     * @param quotaId
     * @param deviceId
     * @param type
     * @return
     */
    List<TrendPoint2> getQuotaTrend( String startTime,String endTime,String quotaId,String deviceId,int type  );


    /**
     * 指标趋势图
     * @param quotaId
     * @param deviceIds
     * @param startTime
     * @param endTime
     * @param type
     * @return
     */
    BoardQuotaVO getBoardData(String quotaId,List<String> deviceIds,String startTime,String endTime,Integer type  );


    /**
     * 获取面板数据
     * @param id
     * @param start
     * @param end
     * @param type
     * @return
     */
    BoardQuotaVO getBoardData(Integer id, String start, String end, Integer type);



}
