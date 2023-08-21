package com.sensonet.influx;

import lombok.extern.slf4j.Slf4j;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.impl.InfluxDBResultMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * This class is used to add data to influxDB and query data from influxDB.
 */
@Component
@Slf4j
public class InfluxRepository {

    @Autowired
    private InfluxDB influxDB;

    @Value("${spring.influx.db}")
    private String dbName;


    /**
     * Add data to influxDB.
     *
     * @param object The object to be added to influxDB.
     */
    public void add(Object object) {
        // Create a point builder by POJO.
        Point.Builder pointBuilder = Point.measurementByPOJO(object.getClass());
        // Construct a point.
        Point point = pointBuilder.addFieldsFromPOJO(object) // Note plusHours(8) is because of the time difference.
                .time(LocalDateTime.now().plusHours(8).toInstant(ZoneOffset.of("+8")).toEpochMilli(), TimeUnit.MILLISECONDS)
                .build();
        influxDB.setDatabase(dbName); // Select the database.
        influxDB.write(point); // Write the point to influxDB.
        influxDB.close(); // Close the connection.
    }


    /**
     * Query data from influxDB.
     *
     * @param ql   The query language.
     * @param clazz The class of the object to be returned.
     * @param <T> The type of the object to be returned.
     * @return The list of the object to be returned.
     */
    public <T> List<T> query(String ql, Class<T> clazz) {
        // Execute the query and get the result.
        QueryResult queryResult = influxDB.query(new Query(ql, dbName));
        // Close the connection.
        influxDB.close();
        // Create a result mapper and then return the list of the object to be returned.
        InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
        return resultMapper.toPOJO(queryResult, clazz);
    }


}
