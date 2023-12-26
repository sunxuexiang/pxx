package com.wanmi.sbc.customer.api.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonObjectMapper {

    private static final Logger logger = LoggerFactory.getLogger(JsonObjectMapper.class);

    private final static ObjectMapper MAPPER = new ObjectMapper();
    static {
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
    }

    //1.对象转化为JSON
    public static String toJSON(Object object){
        try {
            return MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
           logger.error("JsonObjectMapper toJSON find error ");
        }
        return null;
    }

    //2.JSON转化为对象
    public static <T> T toObj(String json,Class<T> target){
        try {
            return MAPPER.readValue(json, target);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("JsonObjectMapper toObj find error");
        }
        return null;
    }

    //3.JSON转化为对象
    public static <T> T convertClass(Object source,Class<T> target) {
        try {
            String sourceJson = MAPPER.writeValueAsString(source);
            return MAPPER.readValue(sourceJson, target);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("JsonObjectMapper convertClass find error");
        }
        return null;
    }
}
