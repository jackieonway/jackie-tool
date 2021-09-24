package com.github.jackieonway.util;

import java.io.IOException;
import java.util.Collection;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.github.jackieonway.util.collection.CollectionUtils;

/**
 * JSON的驼峰和下划线互转帮助类
 * 
 * @author jackie
 *
 */
public enum  StringUtils {

    /**
     * StringUtils 实例
     */
    INSTANCE;

    private static final String A_Z_A_Z_D = "[A-Z]([a-z\\d]+)?";

    /**
     * the empty string {@code ""}.
     * @since 1.0.1
     */
    public static final String EMPTY = "";

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
     * <p> this methods is form apache common lang3 </p>
     *
     * <p>Checks if CharSequence contains a search CharSequence, handling {@code null}.
     * This method uses {@link String#indexOf(String)} if possible.</p>
     *
     * <p>A {@code null} CharSequence will return {@code false}.</p>
     *
     * <pre>
     * StringUtils.contains(null, *)     = false
     * StringUtils.contains(*, null)     = false
     * StringUtils.contains("", "")      = true
     * StringUtils.contains("abc", "")   = true
     * StringUtils.contains("abc", "a")  = true
     * StringUtils.contains("abc", "z")  = false
     * </pre>
     *
     * @param seq  the CharSequence to check, may be null
     * @param searchSeq  the CharSequence to find, may be null
     * @return true if the CharSequence contains the search CharSequence,
     *  false if not or {@code null} string input
     * @since 2.0
     * @since 3.0 Changed signature from contains(String, String) to contains(CharSequence, CharSequence)
     */
    public static boolean contains(final CharSequence seq, final CharSequence searchSeq) {
        if (seq == null || searchSeq == null) {
            return false;
        }
        return CharSequenceUtils.indexOf(seq, searchSeq, 0) >= 0;
    }

    /**
     * 将对象的大写转换为下划线加小写，例如：userName-- user_name
     * 
     * @param object object
     * @return String
     * @throws JsonProcessingException exception
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
     * @param json json string
     * @param clazz class
     * @param <T> target class
     * @return object
     * @throws IOException exception
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

    public static String joining(CharSequence delimiter, Collection<CharSequence> strings){
        return joining(delimiter,"","", strings,"");
    }

    public static String joining(CharSequence delimiter, Collection<CharSequence> strings, CharSequence empty){
        return joining(delimiter,"","", strings,empty);
    }

    public static String joining(CharSequence delimiter, CharSequence prefix, CharSequence suffix,
                                 Collection<CharSequence> strings){
        return joining(delimiter,prefix, suffix, strings,"");
    }

    public static String joining(CharSequence delimiter, CharSequence prefix, CharSequence suffix,
                                 Collection<CharSequence> strings, CharSequence empty){
        StringJoiner stringJoiner = new StringJoiner(delimiter, prefix, suffix);
        if (CollectionUtils.isNotEmpty(strings)){
            strings.forEach(stringJoiner::add);
        }else {
            stringJoiner.setEmptyValue(empty);
        }
        return stringJoiner.toString();
    }

    public static String merge(CharSequence delimiter, String source, String merge){
        return merge(delimiter,"","",source, merge);
    }

    public static String merge(CharSequence delimiter, CharSequence prefix, CharSequence suffix,
                               String source, String merge){
        StringJoiner stringJoiner = new StringJoiner(delimiter, prefix, suffix);
        CharSequence sourceStr = source.replace(prefix, EMPTY).replace(suffix, EMPTY);
        stringJoiner.add(sourceStr);
        StringJoiner mergeStringJoiner = new StringJoiner(delimiter, prefix, suffix);
        CharSequence mergeStr = merge.replace(prefix, EMPTY).replace(suffix, EMPTY);
        mergeStringJoiner.add(mergeStr);
        stringJoiner.merge(mergeStringJoiner);
        return stringJoiner.toString();
    }
}