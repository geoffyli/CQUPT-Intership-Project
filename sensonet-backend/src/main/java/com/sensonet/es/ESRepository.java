package com.sensonet.es;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;
import com.sensonet.dto.DeviceDTO;
//import com.sensonet.dto.DeviceLocation;
import com.sensonet.util.JsonUtil;
import com.sensonet.vo.Pager;
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
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * This class is used to operate the ES. It is used to add, search, update and delete devices.
 * It is also used to search devices by location and search devices by tags.
 */
@Component
@Slf4j
public class ESRepository {


    @Autowired
    private RestHighLevelClient restHighLevelClient; // The rest high level client


    /**
     * Add a device to the ES with rest high level client.
     *
     * @param deviceDTO The device to be added
     */
    public void addDevices(DeviceDTO deviceDTO) {
        // If the device is null, return
        if (deviceDTO == null) return;
        // If the device ID is null, return
        if (deviceDTO.getDeviceId() == null) return;

        // Create a new index request
        IndexRequest request = new IndexRequest("devices");
        try {
            // Serialize the device to json, then convert the json to map
            String json = JsonUtil.serialize(deviceDTO);
            // Set the source and the id of the request
            request.source(json, XContentType.JSON);
            request.id(deviceDTO.getDeviceId());
            restHighLevelClient.index(request, RequestOptions.DEFAULT);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Add device to ES failed, device ID: " + deviceDTO.getDeviceId());
        }
    }


    /**
     * Search the device by the device ID.
     *
     * @param deviceId The device ID
     * @return The device DTO
     */
    public DeviceDTO searchDeviceById(String deviceId) {
        // Create a new search request for the index "devices"
        SearchRequest searchRequest = new SearchRequest("devices");
        // Create a new search source builder in which we can build the query.
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // Set the query to the search _id
        searchSourceBuilder.query(QueryBuilders.termQuery("_id", deviceId));
        searchRequest.source(searchSourceBuilder);
        try {
            // Search the device by the device ID
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            // Get the hits (results) and the count of the search response
            SearchHits hits = searchResponse.getHits();
            long hitsCount = hits.getTotalHits().value;
            // If the count is less than 0, return null
            if (hitsCount <= 0) return null;
            // Get the first hit
            DeviceDTO deviceDTO = null;
            for (SearchHit hit : hits) {
                // Wrap the hit result to a device DTO
                String hitResult = hit.getSourceAsString();
                deviceDTO = JsonUtil.getByJson(hitResult, DeviceDTO.class);
                deviceDTO.setDeviceId(deviceId);
                break;
            }
            return deviceDTO;

        } catch (IOException e) {
            e.printStackTrace();
            log.error("Search device by ID failed, device ID: " + deviceId);
            return null;
        }
    }


    /**
     * Update the device status.
     *
     * @param deviceId The device ID
     * @param status   The status
     * @return If the update is successful, return true. Otherwise, return false.
     */
    public boolean updateStatus(String deviceId, Boolean status) {
        // Create a new update request
        UpdateRequest updateRequest = new UpdateRequest("devices", deviceId)
                .doc("status", status);
        try {
            // Update the device status
            restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Update device status failed, device ID: " + deviceId);
            return false;
        }
    }


    /**
     * Update the device tag
     *
     * @param deviceId The device ID
     * @param tag      The tags
     * @return If the update is successful, return true. Otherwise, return false.
     */
    public boolean updateDeviceTag(String deviceId, String tag) {
        // Create a new update request
        UpdateRequest updateRequest = new UpdateRequest("devices", deviceId)
                .doc("tag", tag);
        try {
            // Update the device tag
            restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Update device tag failed, device ID: " + deviceId);
            return false;
        }
    }


    /**
     * Update the device alarm information.
     *
     * @param deviceDTO The device DTO
     */
    public void updateDevicesAlarm(DeviceDTO deviceDTO) {
        // Create a new update request
        UpdateRequest updateRequest = new UpdateRequest("devices", deviceDTO.getDeviceId())
                .doc("alarm", deviceDTO.getAlarm(), // whether the device is in alarm
                        "level", deviceDTO.getLevel(),  // the level of the alarm
                        "alarmName", deviceDTO.getAlarmName()); // the name of the alarm
        try {
            // Update the device alarm information
            restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Update device alarm failed, device ID: " + deviceDTO.getDeviceId());
        }
    }

    /**
     * Update the online status of the device.
     *
     * @param deviceId The device ID
     * @param online   The online status
     */
    public void updateOnline(String deviceId, Boolean online) {
        // Create a new update request
        UpdateRequest updateRequest = new UpdateRequest("devices", deviceId)
                .doc("online", online);
        try {
            // Update the online status of the device
            restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Update device online status failed, device ID: " + deviceId);
        }
    }


    /**
     * Search the devices by the device ID, tags and the state.
     *
     * @param page     The page number
     * @param pageSize The page size
     * @param deviceId The device ID
     * @param tag      The tag
     * @param state    The state
     * @return The pager of the device DTO
     */
    public Pager<DeviceDTO> searchDevice(Long page, Long pageSize, String deviceId, String tag, Integer state) {
        // Create a new search request for the index "devices"
        SearchRequest searchRequest = new SearchRequest("devices");
        // Create a new search source builder in which we can build the query.
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        // Bool query
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        /*
        Construct the bool query
         */
        // for the device ID
        if (!Strings.isNullOrEmpty(deviceId)) {
            // Use wildcard query to search the device ID
            boolQueryBuilder.must(QueryBuilders.wildcardQuery("deviceId", deviceId + "*"));
        }
        // for the device tag
        if (!Strings.isNullOrEmpty(tag)) {
            boolQueryBuilder.must(QueryBuilders.wildcardQuery("tag", "*" + tag + "*"));
        }
        // for the device state
        /*
        0: online
        1: offline
        2: normal alarm
        3: serious alarm
         */
        if (state != null) {
            boolQueryBuilder.must(QueryBuilders.termQuery("status", true));
            if (state == 0)
                boolQueryBuilder.must(QueryBuilders.termQuery("online", true));
            else if (state == 1)
                boolQueryBuilder.must(QueryBuilders.termQuery("online", false));
            else if (state == 2)
                boolQueryBuilder.must(QueryBuilders.termQuery("level", 1));
            else if (state == 3)
                boolQueryBuilder.must(QueryBuilders.termQuery("level", 2));
        }

        // Pagination
        sourceBuilder.from((page.intValue() - 1) * pageSize.intValue());
        sourceBuilder.size(pageSize.intValue());
        sourceBuilder.trackTotalHits(true); // get the total hits
        sourceBuilder.sort("level", SortOrder.DESC); // Sort by the level of the alarm
        // Add the query to the search source builder
        sourceBuilder.query(boolQueryBuilder);

        // Add the search source builder to the search request
        searchRequest.source(sourceBuilder);

        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits searchHits = searchResponse.getHits();
            // Construct the device DTO list
            List<DeviceDTO> devices = Lists.newArrayList();
            // Iterate the search hits and construct the device DTO list
            for (SearchHit hit : searchHits) {
                String hitResult = hit.getSourceAsString();
                DeviceDTO deviceDTO = JsonUtil.getByJson(hitResult, DeviceDTO.class);
                devices.add(deviceDTO);
            }

            // Construct the pager and set the items
            Pager<DeviceDTO> pager = new Pager<>(searchResponse.getHits().getTotalHits().value, pageSize);
            pager.setItems(devices);
            return pager;
        } catch (IOException e) {
            e.printStackTrace();
            log.error("查询设备异常");
            return null;
        }


    }


    /**
     * Count the number of all devices.
     *
     * @return The number of all devices
     */
    public Long getAllDeviceCount() {

        CountRequest countRequest = new CountRequest("devices");
        countRequest.query(QueryBuilders.matchAllQuery());

        try {
            CountResponse response = restHighLevelClient.count(countRequest, RequestOptions.DEFAULT);
            return response.getCount();
        } catch (IOException e) {
            e.printStackTrace();
            return 0L;
        }

    }


    /**
     * Count the number of all offline devices.
     *
     * @return The number of all offline devices
     */
    public Long getOfflineCount() {

        CountRequest countRequest = new CountRequest("devices");
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery("online", false));

        countRequest.query(boolQueryBuilder);

        try {
            CountResponse response = restHighLevelClient.count(countRequest, RequestOptions.DEFAULT);
            return response.getCount();
        } catch (IOException e) {
            e.printStackTrace();
            return 0L;
        }

    }


    /**
     * Count the number of all devices with alarm.
     *
     * @return The number of all devices with alarm
     */
    public Long getAlarmCount() {

        CountRequest countRequest = new CountRequest("devices");
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery("online", true));
        boolQueryBuilder.must(QueryBuilders.termQuery("alarm", true));
        countRequest.query(boolQueryBuilder);

        try {
            CountResponse response = restHighLevelClient.count(countRequest, RequestOptions.DEFAULT);
            return response.getCount();
        } catch (IOException e) {
            e.printStackTrace();
            return 0L;
        }

    }


}
