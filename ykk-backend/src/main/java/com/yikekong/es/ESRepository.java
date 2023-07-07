package com.yikekong.es;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;
import com.yikekong.dto.DeviceDTO;
import com.yikekong.dto.DeviceLocation;
import com.yikekong.util.JsonUtil;
import com.yikekong.vo.Pager;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.geo.GeoDistance;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.GeoDistanceQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class ESRepository {


    @Autowired
    private RestHighLevelClient restHighLevelClient;


    /**
     * 添加设备
     * @param deviceDTO
     */
    public  void addDevices(DeviceDTO deviceDTO){
        if(deviceDTO==null ) return;
        if(deviceDTO.getDeviceId()==null) return;
        IndexRequest request=new IndexRequest("devices");
        try {
            String json = JsonUtil.serialize(deviceDTO);
            Map map = JsonUtil.getByJson(json, Map.class);
            request.source(map);
            request.id(deviceDTO.getDeviceId());
            restHighLevelClient.index(request, RequestOptions.DEFAULT);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            log.error("设备添加发生异常");
        }
    }


    /**
     * 根据设备id 查询设备
     * @param deviceId  设备id
     * @return
     */
    public DeviceDTO searchDeviceById(String deviceId){
        SearchRequest searchRequest=new SearchRequest("devices");
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termQuery("_id",deviceId));
        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            SearchHits hits = searchResponse.getHits();
            long hitsCount = hits.getTotalHits().value;
            if(hitsCount<=0) return null;
            DeviceDTO deviceDTO=null;
            for(SearchHit hit:hits){
                String hitResult = hit.getSourceAsString();
                deviceDTO=JsonUtil.getByJson(hitResult,DeviceDTO.class  );
                deviceDTO.setDeviceId(deviceId);
                break;
            }
            return deviceDTO;

        } catch (IOException e) {
            e.printStackTrace();
            log.error("查询设备异常");
            return null;
        }
    }


    /**
     * 更新设备状态
     * @param deviceId
     * @param status
     * @return
     */
    public boolean updateStatus(String deviceId,Boolean status){
        UpdateRequest updateRequest=new UpdateRequest("devices",deviceId)
                .doc( "status",status );
        try {
            restHighLevelClient.update( updateRequest,RequestOptions.DEFAULT );
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            log.error("更新设备状态出错");
            return false;
        }
    }


    /**
     * 更新设备标签
     * @param deviceId
     * @param tags
     * @return
     */
    public boolean updateDeviceTag(String deviceId,String tags){
        UpdateRequest updateRequest=new UpdateRequest("devices",deviceId)
                .doc( "tag",tags );
        try {
            restHighLevelClient.update( updateRequest,RequestOptions.DEFAULT );
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            log.error("更新设备标签出错");
            return false;
        }
    }


    /**
     * 更新设备告警信息
     * @param deviceDTO
     * @return
     */
    public boolean updateDevicesAlarm(DeviceDTO deviceDTO){
        UpdateRequest updateRequest=new UpdateRequest("devices",deviceDTO.getDeviceId())
                .doc(   "alarm",deviceDTO.getAlarm(),//是否告警
                        "level",deviceDTO.getLevel(),//告警级别
                        "alarmName",deviceDTO.getAlarmName() );//告警名称
        try {
            restHighLevelClient.update( updateRequest,RequestOptions.DEFAULT );
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            log.error("更新设备告警信息出错");
            return false;
        }
    }

    /**
     * 更新在线状态
     * @param deviceId
     * @param online
     * @return
     */
    public boolean updateOnline(String deviceId,Boolean online){
        UpdateRequest updateRequest=new UpdateRequest("devices",deviceId)
                .doc( "online",online );
        try {
            restHighLevelClient.update( updateRequest,RequestOptions.DEFAULT );
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            log.error("更新在线状态出错");
            return false;
        }
    }


    public Pager<DeviceDTO> searchDevice(Long page,Long pageSize,String deviceId,String tags,Integer state){

        SearchRequest searchRequest=new SearchRequest("devices");

        SearchSourceBuilder sourceBuilder=new SearchSourceBuilder();

        BoolQueryBuilder boolQueryBuilder=QueryBuilders.boolQuery();

        //设备编号
        if( !Strings.isNullOrEmpty(deviceId) ){
            boolQueryBuilder.must(  QueryBuilders.wildcardQuery("deviceId", deviceId+"*" )  );
        }

        //标签
        if( !Strings.isNullOrEmpty(tags) ){
            boolQueryBuilder.must(  QueryBuilders.wildcardQuery("tag", "*"+tags+"*" )  );
        }

        //状态  在线、离线、一般报警，严重报警
        if( state!=null ){
            boolQueryBuilder.must( QueryBuilders.termQuery("status",true) );
            //在线
            if(state.intValue()==0){
                boolQueryBuilder.must( QueryBuilders.termQuery("online",true) );
            }
            //离线
            if(state.intValue()==1){
                boolQueryBuilder.must( QueryBuilders.termQuery("online",false) );
            }
            //一般报警
            if(state.intValue()==2){
                boolQueryBuilder.must( QueryBuilders.termQuery("level",1) );
            }
            //严重报警
            if(state.intValue()==3){
                boolQueryBuilder.must( QueryBuilders.termQuery("level",2) );
            }
        }

        //分页
        sourceBuilder.from(  (page.intValue()-1) * pageSize.intValue()  );
        sourceBuilder.size( pageSize.intValue() );
        sourceBuilder.trackTotalHits(true);//分页查询，可以得到全部的结果数

        sourceBuilder.sort("level", SortOrder.DESC);//告警严重的排在前面

        sourceBuilder.query(boolQueryBuilder);
        searchRequest.source(sourceBuilder);


        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            SearchHits searchHits = searchResponse.getHits();

            List<DeviceDTO> devices= Lists.newArrayList();
            for( SearchHit hit:searchHits ){
                String hitResult = hit.getSourceAsString();
                DeviceDTO deviceDTO=JsonUtil.getByJson(hitResult,DeviceDTO.class);
                devices.add(deviceDTO);
            }

            Pager<DeviceDTO> pager=new Pager<>( searchResponse.getHits().getTotalHits().value,pageSize );
            pager.setItems( devices );

            return pager;

        } catch (IOException e) {
            e.printStackTrace();
            log.error("查询设备异常");
            return null;
        }


    }


    /**
     * 统计所有设备数量
     * @return
     */
    public Long getAllDeviceCount(){

        CountRequest countRequest=new CountRequest("devices");
        countRequest.query( QueryBuilders.matchAllQuery() );

        try {
            CountResponse response = restHighLevelClient.count(countRequest, RequestOptions.DEFAULT);
            return response.getCount();
        } catch (IOException e) {
            e.printStackTrace();
            return 0L;
        }

    }


    /**
     * 统计所有离线设备数量
     * @return
     */
    public Long getOfflineCount(){

        CountRequest countRequest=new CountRequest("devices");
        BoolQueryBuilder boolQueryBuilder=QueryBuilders.boolQuery();
        boolQueryBuilder.must( QueryBuilders.termQuery("online",false)  );

        countRequest.query( boolQueryBuilder );

        try {
            CountResponse response = restHighLevelClient.count(countRequest, RequestOptions.DEFAULT);
            return response.getCount();
        } catch (IOException e) {
            e.printStackTrace();
            return 0L;
        }

    }


    /**
     * 统计所有告警设备数量
     * @return
     */
    public Long getAlarmCount(){

        CountRequest countRequest=new CountRequest("devices");
        BoolQueryBuilder boolQueryBuilder=QueryBuilders.boolQuery();
        boolQueryBuilder.must( QueryBuilders.termQuery("online",true)  );
        boolQueryBuilder.must( QueryBuilders.termQuery("alarm",true)  );
        countRequest.query( boolQueryBuilder );

        try {
            CountResponse response = restHighLevelClient.count(countRequest, RequestOptions.DEFAULT);
            return response.getCount();
        } catch (IOException e) {
            e.printStackTrace();
            return 0L;
        }

    }


    /**
     * 更新设备gps信息
     * @param deviceLocation
     */
    public void saveLocation(DeviceLocation deviceLocation){

        IndexRequest request=new IndexRequest("gps");
        request.source("location", deviceLocation.getLocation() );
        request.id( deviceLocation.getDeviceId() );
        try {
            restHighLevelClient.index(request,RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("更新gps出错");
        }

    }


    /**
     * 搜索一定距离内的设备列表
     * @param lat
     * @param lon
     * @param distance
     * @return
     */
    public List<DeviceLocation> searchDeviceLocation(Double lat,Double lon,Integer distance){


        //构建查询

        SearchRequest searchRequest=new SearchRequest("gps");

        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();

        GeoDistanceQueryBuilder geoDistanceQueryBuilder=new GeoDistanceQueryBuilder("location");
        geoDistanceQueryBuilder.distance( distance, DistanceUnit.KILOMETERS);
        geoDistanceQueryBuilder.point( lat,lon );

        searchSourceBuilder.query(geoDistanceQueryBuilder );

        //排序
        GeoDistanceSortBuilder distanceSortBuilder=new GeoDistanceSortBuilder("location",lat,lon);
        distanceSortBuilder.unit(DistanceUnit.KILOMETERS);
        distanceSortBuilder.order(SortOrder.ASC);//SortOrder.ASC 升序（由近到远）；
        distanceSortBuilder.geoDistance( GeoDistance.ARC );//GeoDistance.ARC  精准度高，计算较慢

        searchSourceBuilder.sort(distanceSortBuilder);


        searchSourceBuilder.from(0);
        searchSourceBuilder.size(200);

        searchRequest.source( searchSourceBuilder );


        //封装结果

        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = searchResponse.getHits();
            if(hits.getTotalHits().value<=0){
                return Lists.newArrayList();
            }

            List<DeviceLocation> deviceLocationList=Lists.newArrayList();
            Arrays.stream( hits.getHits() ).forEach( h->{
                DeviceLocation deviceLocation=new DeviceLocation();
                deviceLocation.setDeviceId(h.getId()  );
                deviceLocation.setLocation( h.getSourceAsMap().get("location").toString() );
                deviceLocationList.add(deviceLocation);
            } );
            return deviceLocationList;

        } catch (IOException e) {
            e.printStackTrace();
            return Lists.newArrayList();
        }

    }



}
