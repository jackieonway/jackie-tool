package com.github.jackieonway.util.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

/**
 * 自定义响应结构
 * @author Jackie
 */
public enum  JsonUtils {
    /**
     * JsonUtils 实例
     */
    INSTANCE;

    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);

    // 定义jackson对象
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 将对象转换成json字符串。
     * <p>Title: pojoToJson</p>
     * <p>Description: </p>
     * @param data  data
     * @return object string
     */
    public static String objectToJson(Object data) {
    	try {
            return MAPPER.writeValueAsString(data);
		} catch (JsonProcessingException e) {
            logger.error("对象转 JSON 失败",e);
		}
    	return null;
    }
    
    /**
     * 将json结果集转化为对象
     * 
     * @param data json数据
     * @param beanType 对象中的object类型
     * @param <T> target class
     * @return object
     */
    public static <T> T jsonToPojo(String data, Class<T> beanType) {
        try {
            return MAPPER.readValue(data, beanType);
        } catch (Exception e) {
            logger.error("JSON 转对象失败",e);
        }
        return null;
    }
    
    /**
     * 将json数据转换成pojo对象list
     * <p>Title: jsonToList</p>
     * <p>Description: </p>
     * @param jsonData jsonData
     * @param beanType beanType
     * @param <T> target class
     * @return objects
     */
    public static <T>List<T> jsonToList(String jsonData, Class<T> beanType) {
    	JavaType javaType = MAPPER.getTypeFactory().constructParametricType(List.class, beanType);
    	try {
            return MAPPER.readValue(jsonData, javaType);
		} catch (Exception e) {
            logger.error("JSON 转对象列表失败",e);
		}
    	
    	return Collections.emptyList();
    }
}
