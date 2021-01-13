package com.github.jackieonway.util.pinyin4j;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jackie
 */
public class Pinyin4jUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(Pinyin4jUtils.class);
    private Pinyin4jUtils() {
    }

    public static String converterToFirstSpell(String chineseName) {
        StringBuilder pinyinName = new StringBuilder();
        char[] nameChar = chineseName.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

        for (char aNameChar : nameChar) {
            if (aNameChar > 128) {
                try {
                    pinyinName.append(PinyinHelper.toHanyuPinyinStringArray(aNameChar, defaultFormat)[0].charAt(0));
                } catch (BadHanyuPinyinOutputFormatCombination var6) {
                    LOGGER.error("获取首字符失败", var6);
                }
            } else {
                pinyinName.append(aNameChar);
            }
        }
        return pinyinName.toString();
    }

    /**
     * 将汉字转换为全拼
     * @param src 汉字字符串
     * @return 全拼
     */
    public static String getPinYin(String src){
        char[] hz;
        //该方法的作用是返回一个字符数组，该字符数组中存放了当前字符串中的所有字符
        hz = src.toCharArray();
        //该数组用来存储
        String[] py;
        //设置汉子拼音输出的格式
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);
        //存放拼音字符串
        StringBuilder pys = new StringBuilder();

        try {
            for (char aHz : hz) {
                //先判断是否为汉字字符
                if (Character.toString(aHz).matches("[\\u4E00-\\u9FA5]+")) {
                    //将汉字的几种全拼都存到py数组中
                    py = PinyinHelper.toHanyuPinyinStringArray(aHz, format);
                    //取出改汉字全拼的第一种读音，并存放到字符串pys后
                    pys.append(py[0]);
                } else {
                    //如果不是汉字字符，间接取出字符并连接到 pys 后
                    pys.append(aHz);
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination e){
            LOGGER.error("获取拼音失败",e);
        }
        return pys.toString();
    }

    /**
     * 提取每个汉字的首字母
     * @param str 汉字
     * @return 每个汉字首字母
     */
    public static String getPinYinHeadChar(String str){
        StringBuilder convert = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char word = str.charAt(i);
            //提取汉字的首字母
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
            if (pinyinArray != null){
                convert.append(pinyinArray[0].charAt(0));
            }else{
                convert.append(word);
            }
        }
        return convert.toString().toUpperCase();
    }

    /**
     * 将字符串转换成ASCII码
     */
    public static String getCnAscii(String str){
        StringBuilder buf = new StringBuilder();
        //将字符串转换成字节序列
        byte[] bGbk = str.getBytes();
        for (byte gbk : bGbk) {
            //将每个字符转换成ASCII码
            buf.append(String.format("%02X", gbk & 0xff));
        }
        return buf.toString();
    }
}
