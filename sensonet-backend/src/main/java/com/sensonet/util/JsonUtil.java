package com.sensonet.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;

public class JsonUtil {
    /**
     * Turn a json string into an object
     *
     * @param json  json string
     * @param clazz object class
     * @param <T>   object type
     * @return object
     * @throws IOException
     */
    public static <T> T getByJson(String json, Class<T> clazz) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        // Ignore unknown properties
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // Set the time format: yyyy-MM-dd'T'HH:mm:ss.SSSZ
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        // Deserialize "2020-01-22T11:11:11" string to LocalDateTime format
        mapper.registerModule(new JavaTimeModule());

        return mapper.readValue(json, clazz);
    }

    /**
     * Turn an object into a json string
     *
     * @param object
     * @return
     * @throws JsonProcessingException
     */
    public static String serialize(Object object) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        // The time format: yyyy-MM-dd'T'HH:mm:ss.SSSZ
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        // Serialize LocalDateTime format to "2020-01-22T11:11:11" string
        mapper.registerModule(new JavaTimeModule());

        return mapper.writeValueAsString(object);
    }
}
