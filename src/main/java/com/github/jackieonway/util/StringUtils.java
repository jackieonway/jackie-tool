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
public class StringUtils {

    /**
     * 将对象的大写转换为下划线加小写，例如：userName-- user_name
     * 
     * @param object
     * @return
     * @throws JsonProcessingException
     */
    public static String toUnderlineJSONString(Object object) throws JsonProcessingException{
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
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
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.PASCAL_CASE_TO_CAMEL_CASE);
        return mapper.readValue(json, clazz);
    }
    
    /**
     * 下划线转驼峰法
     * @param line 源字符串
     * @param smallCamel 大小驼峰,是否为小驼峰  true 小驼峰 eg:userName false 大驼峰 eg:UserName 
     * @return 转换后的字符串
     */
    public static String underline2Camel(String line,boolean smallCamel){
        if(line==null||"".equals(line)){
            return "";
        }
        StringBuilder sb=new StringBuilder();
        Pattern pattern=Pattern.compile("([A-Za-z\\d]+)(_)?");
        Matcher matcher=pattern.matcher(line);
        while(matcher.find()){
            String word=matcher.group();
            sb.append(smallCamel&&matcher.start()==0?Character.toLowerCase(word.charAt(0)):Character.toUpperCase(word.charAt(0)));
            int index=word.lastIndexOf('_');
            if(index>0){
                sb.append(word.substring(1, index).toLowerCase());
            }else{
                sb.append(word.substring(1).toLowerCase());
            }
        }
        return sb.toString();
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
        Pattern pattern=Pattern.compile("[A-Z]([a-z\\d]+)?");
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
    
    public static void main(String[] args) {
		String s= "userNameSdsDD";
		String ss = "<xml>"+
  "<return_code>SUCCESS</return_code>"+
  "<return_msg>OK</return_msg>"+
  "<result_code>SUCCESS</result_code>"+
  "<mch_id>100010</mch_id>"+
  "<nonce_str>AuBW53Js00QB9aS7</nonce_str>"+
  "<sign>1B09E3D0E547665F807CAD8B1D556D4B</sign>"+
  "<out_trade_no>5812281</out_trade_no>"+
  "<code_url>weixin://wxpay/bizpayurl?pr=LLN0u7P</code_url>"+
	  "<prepay_id>wx20161214221028392ab45f510474991331</prepay_id>"+
	  "<trade_type>NATIVE</trade_type>"+
	  "</xml>";
		System.out.println(StringUtils.camel2Underline(s,true));
		System.out.println(StringUtils.camel2Underline(s,false));
		System.out.println(StringUtils.underline2Camel(ss,true));
		System.out.println(StringUtils.underline2Camel(ss,false));
	}
}