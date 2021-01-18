package com.github.jackieonway.util;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

/**
 * JSON的驼峰和下划线互转帮助类
 * 
 * @author yangzhilong
 *
 */
public enum  StringUtils {

    /**
     * StringUtils 实例
     */
    INSTANCE;

    private static final String A_Z_A_Z_D = "[A-Z]([a-z\\d]+)?";


    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static boolean isNotEmpty(CharSequence cs) {
        return !isEmpty(cs);
    }

    public static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs != null && (strLen = cs.length()) != 0) {
            for(int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(cs.charAt(i))) {
                    return false;
                }
            }

            return true;
        } else {
            return true;
        }
    }

    public static boolean isNotBlank(CharSequence cs) {
        return !isBlank(cs);
    }

    public static boolean equals(final CharSequence cs1, final CharSequence cs2) {
        if (cs1 == cs2) {
            return true;
        }
        if (cs1 == null || cs2 == null) {
            return false;
        }
        if (cs1.length() != cs2.length()) {
            return false;
        }
        if (cs1 instanceof String && cs2 instanceof String) {
            return cs1.equals(cs2);
        }
        return CharSequenceUtils.regionMatches(cs1, false, 0, cs2, 0, cs1.length());
    }

    /**
     * 将对象的大写转换为下划线加小写，例如：userName-- user_name
     * 
     * @param object
     * @return
     * @throws JsonProcessingException
     */
    public static String toUnderlineJSONString(Object object) throws JsonProcessingException{
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        mapper.setSerializationInclusion(Include.NON_NULL);
        return mapper.writeValueAsString(object);
    }

    /**
     * 将下划线转换为驼峰的形式，例如：user_name--userName
     * 
     * @param json
     * @param clazz
     * @return
     * @throws IOException
     */
    public static <T> T toSnakeObject(String json, Class<T> clazz) throws IOException{
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE);
        return mapper.readValue(json, clazz);
    }

    /**
     * 驼峰法转下划线
     * @param line 源字符串
     * @param upOrLow 是否大小写  true 大写 eg:USER_NAME false 小写 eg:user_name
     * @return 转换后的字符串
     */
    public static String camel2Underline(String line,boolean upOrLow){
        if(line==null||"".equals(line)){
            return "";
        }
    	line=String.valueOf(line.charAt(0)).toUpperCase().concat(line.substring(1));
        StringBuilder sb=new StringBuilder();
        Pattern pattern=Pattern.compile(A_Z_A_Z_D);
        Matcher matcher=pattern.matcher(line);
        while(matcher.find()){
            String word=matcher.group();
            if (upOrLow) {
            	sb.append(word.toUpperCase());
			}else {
				sb.append(word.toLowerCase());
			}
            sb.append(matcher.end()==line.length()?"":"_");
        }
        return sb.toString();
    }
}