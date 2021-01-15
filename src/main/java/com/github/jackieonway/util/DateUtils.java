package com.github.jackieonway.util;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Jackie
 */
public enum  DateUtils {

    /**
     * DateUtils 实例
     */
    INSTANCE;

    private static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    private static final Logger log = LoggerFactory.getLogger(DateUtils.class);

    private static final String[] ZODIAC_ARR = {"猴", "鸡", "狗", "猪", "鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊"};

    private static final String[] CONSTELLATION_ARR = {"水瓶座", "双鱼座", "白羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "魔羯座"};

    private static final int[] CONSTELLATION_EDAGE_DAY = {20, 19, 21, 21, 21, 22, 23, 23, 23, 23, 22, 22};
    /**
     * 默认时间格式化格式
     */
    private static final String DEFAULT_FORMAT = YYYY_MM_DD_HH_MM_SS;
    /**
     * 默认时区 东八时区
     */
    private static final String DEFAULT_TIME_ZONE = "GMT+8";

    /**
     *  Date 转化为String
     * @param date 时间
     * @param pattern 格式化格式
     * @return String时间
     */
    public static synchronized String formatDate(Date date ,String pattern){
        try {
            if (StringUtils.isEmpty(pattern)){
                pattern = DEFAULT_FORMAT;
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            return simpleDateFormat.format(date);
        }catch (Exception e){
            log.info("Date format error : {}",e.getMessage());
            return null;
        }
    }

    /**
     * 按照指定格式格式化日期
     * @param date 待格式化的日期
     * @param pattern 格式化规则
     * @return 格式化后的日期
     */
    public static Date parse(String date , String pattern) {
        if (StringUtils.isEmpty(pattern)){
            pattern = DEFAULT_FORMAT;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        try {
            return dateFormat.parse(date);
        } catch (Exception e) {
            try {
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(DEFAULT_TIME_ZONE));
                calendar.setTimeInMillis(Long.parseLong(date));
                return calendar.getTime();
            }catch (Exception e1){
                return null;
            }
        }
    }

    /**
     * 以当天凌晨为时间点增加天数获得日期
     * @param i 天数(可为负值)
     * */
    public static Date addDayAtZero(int i){
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(DEFAULT_TIME_ZONE));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        calendar.add(Calendar.DAY_OF_MONTH, i);
        return calendar.getTime();
    }
    /**
     * 以当周凌晨为时间点增加周数获得日期
     * @param i 天数(可为负值)
     * */
    public static Date addWeekAtZero(int i){
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(DEFAULT_TIME_ZONE));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.DAY_OF_WEEK, 2);
        calendar.add(Calendar.WEEK_OF_MONTH, i);
        return calendar.getTime();
    }
    /**
     * 以当月凌晨为时间点增加月数获得日期
     * @param i 天数(可为负值)
     * */
    public static Date addMoonAtZero(int i){
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(DEFAULT_TIME_ZONE));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.MONTH, i);
        return calendar.getTime();
    }
    /**
     * 以当年凌晨为时间点增加年数获得日期
     * @param i 天数(可为负值)
     * */
    public static Date addYearAtZero(int i){
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(DEFAULT_TIME_ZONE));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.MONTH, 0);
        calendar.add(Calendar.YEAR, i);
        return calendar.getTime();
    }

    /**
     * 获取当前年
     * @return Integer数据- 年
     */
    public static Integer getCurrentYear(){
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(DEFAULT_TIME_ZONE));
        return  calendar.get(Calendar.YEAR);
    }

    /**
     * 获取任意时间
     * @param year 年
     * @param month 月
     * @param day 日
     * @param hour 时
     * @param minute 分
     * @param second 秒
     * @param millisecond 毫秒
     * @return 时间
     */
    public static Date getDate(Integer year, Integer month, Integer day , Integer hour , Integer minute , Integer second , Integer millisecond){
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(DEFAULT_TIME_ZONE));
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month-1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, millisecond);
        return calendar.getTime();
    }

    /**
     * 获取当前日期的近期天数
     * @param num 距离今天的天数 默认 0:表示当前时间
     * @return 时间
     */
    public static Date getDay(Integer num){
        if (num == null){
            num = 0;
        }
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(DEFAULT_TIME_ZONE));
        calendar.set(Calendar.DAY_OF_YEAR,calendar.get(Calendar.DAY_OF_YEAR) + num);
        return calendar.getTime();
    }

    /**
     * 获取当前日期的近期周数
     * @param num 距离今天的周数 默认 0:表示当前时间
     * @return 时间
     */
    public static Date getWeek(Integer num){
        if (num == null){
            num = 0;
        }
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(DEFAULT_TIME_ZONE));
        calendar.set(Calendar.WEEK_OF_YEAR,calendar.get(Calendar.WEEK_OF_YEAR) + num);
        return calendar.getTime();
    }

    /**
     * 获取当前日期的近期月数
     * @param num 距离今天的月数 默认 0:表示当前时间
     * @return 时间
     */
    public static Date getMonth(Integer num){
        if (num == null){
            num = 0;
        }
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(DEFAULT_TIME_ZONE));
        calendar.set(Calendar.MONTH,calendar.get(Calendar.MONTH) + num);
        return calendar.getTime();
    }

    /**
     * 获取当前日期的近期年数
     * @param num 距离今天的年数 默认 0:表示当前时间
     * @return 时间
     */
    public static Date getYear(Integer num){
        if (num == null){
            num = 0;
        }
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(DEFAULT_TIME_ZONE));
        calendar.set(Calendar.YEAR,calendar.get(Calendar.YEAR) + num);
        return calendar.getTime();
    }

    /**
     * 计算i天的毫秒值
     * */
    public static Long shortOfDay(int i){
        return 86400000L*i;
    }

    public static Date convertStringToDate(String aMask, String strDate)
            throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat(aMask);

        if (log.isDebugEnabled()) {
            log.debug("converting '{}}' to date with mask '{}}'",strDate, aMask);
        }
        Date date;
        try {
            date = df.parse(strDate);
        } catch (ParseException pe) {
            throw new ParseException(pe.getMessage(), pe.getErrorOffset());
        }
        return date;
    }


    private static String getDateTime(String aMask, Date aDate) {
        if (aDate == null) {
            return "";
        }
        SimpleDateFormat df = new SimpleDateFormat(aMask);
        return df.format(aDate);
    }


    public static String convertDateToString(Date aDate, String pattern) {
        return getDateTime(pattern, aDate);
    }

    /**
     * 根据日期获取生肖
     *
     * @return
     */
    public static String getZodica(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return ZODIAC_ARR[cal.get(Calendar.YEAR) % 12];
    }

    /**
     * 根据日期获取星座
     *
     * @return
     */
    public static String getConstellation(Date date) {
        if (date == null) {
            return "";
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        if (day < CONSTELLATION_EDAGE_DAY[month]) {
            month = month - 1;
        }
        if (month >= 0) {
            return CONSTELLATION_ARR[month];
        }
        // default to return 魔羯
        return CONSTELLATION_ARR[11];
    }


    /**
     * 获取当天的开始时间
     *
     * @return yyyy-MM-dd HH:mm:ss  格式
     */
    public static Date getDayBegin() {
        Date date = new Date();
        return getDayBeginTime(date);
    }

    /**
     * 获取当天的结束时间
     *
     * @return yyyy-MM-dd HH:mm:ss  格式
     */
    public static Date getDayEnd() {
        Date date = new Date();
        return getDayEndTime(date);
    }

    /**
     * 获取昨天的开始时间
     *
     * @return 默认格式 Wed May 31 14:47:18 CST 2017
     */
    public static Date getBeginDayOfYesterday() {
        Calendar cal = new GregorianCalendar();
        cal.setTime(getDayBegin());
        cal.add(Calendar.DAY_OF_MONTH, -1);
        return cal.getTime();
    }

    /**
     * 获取昨天的结束时间
     *
     * @return 默认格式 Wed May 31 14:47:18 CST 2017
     */
    public static Date getEndDayOfYesterDay() {
        Calendar cal = new GregorianCalendar();
        cal.setTime(getDayEnd());
        cal.add(Calendar.DAY_OF_MONTH, -1);
        return cal.getTime();
    }

    /**
     * 获取明天的开始时间
     *
     * @return 默认格式 Wed May 31 14:47:18 CST 2017
     */
    public static Date getBeginDayOfTomorrow() {
        Calendar cal = new GregorianCalendar();
        cal.setTime(getDayBegin());
        cal.add(Calendar.DAY_OF_MONTH, 1);

        return cal.getTime();
    }

    /**
     * 获取明天的结束时间
     *
     * @return 默认格式 Wed May 31 14:47:18 CST 2017
     */
    public static Date getEndDayOfTomorrow() {
        Calendar cal = new GregorianCalendar();
        cal.setTime(getDayEnd());
        cal.add(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }

    /**
     * 获取本周的开始时间
     *
     * @return yyyy-MM-dd HH:mm:ss  格式
     */
    public static Date getBeginDayOfWeek() {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayofweek = cal.get(Calendar.DAY_OF_WEEK);
        if (dayofweek == 1) {
            dayofweek += 7;
        }
        cal.add(Calendar.DATE, 2 - dayofweek);
        return getDayBeginTime(cal.getTime());
    }

    /**
     * 获取本周的结束时间
     *
     * @return yyyy-MM-dd HH:mm:ss  格式
     */
    public static Date getEndDayOfWeek() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getBeginDayOfWeek());
        cal.add(Calendar.DAY_OF_WEEK, 6);
        Date weekEndSta = cal.getTime();
        return getDayEndTime(weekEndSta);
    }

    /**
     * 获取上周的开始时间
     *
     * @return yyyy-MM-dd HH:mm:ss  格式
     */
    public static Date getBeginDayOfBeforeWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.add(Calendar.DATE, -7);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return getDayBeginTime(calendar.getTime());
    }

    /**
     * 获取上周的结束时间
     *
     * @return yyyy-MM-dd HH:mm:ss  格式
     */
    public static Date getEndDayOfBeforeWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.add(Calendar.DATE, -7);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        return getDayEndTime(calendar.getTime());
    }

    /**
     * 获取指定时间所在周的开始时间
     *
     * @return yyyy-MM-dd HH:mm:ss  格式
     */
    public static Date getBeginDayOfWeekByDate( Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayofweek = cal.get(Calendar.DAY_OF_WEEK);
        if (dayofweek == 1) {
            dayofweek += 7;
        }
        cal.add(Calendar.DATE, 2 - dayofweek);
        return getDayBeginTime(cal.getTime());
    }

    /**
     * 获取指定时间所在周的结束时间
     *
     * @return yyyy-MM-dd HH:mm:ss  格式
     */
    public static Date getEndDayOfWeekByDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getBeginDayOfWeekByDate(date));
        cal.add(Calendar.DAY_OF_WEEK, 6);
        Date weekEndSta = cal.getTime();
        return getDayEndTime(weekEndSta);
    }

    /**
     * 获取下月的开始时间
     *
     * @return yyyy-MM-dd HH:mm:ss  格式
     */
    public static Date getBeginDayOfNextMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(getNowYear(), getNowMonth() , 1);
        return getDayBeginTime(calendar.getTime());
    }

    /**
     * 获取下月的结束时间
     *
     * @return yyyy-MM-dd HH:mm:ss  格式
     */
    public static Date getEndDayOfNextMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(getNowYear(), getNowMonth() , 1);
        int day = calendar.getActualMaximum(5);
        calendar.set(getNowYear(), getNowMonth() , day);
        return getDayEndTime(calendar.getTime());
    }

    /**
     * 获取本月的开始时间
     *
     * @return yyyy-MM-dd HH:mm:ss  格式
     */
    public static Date getBeginDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(getNowYear(), getNowMonth() - 1, 1);
        return getDayBeginTime(calendar.getTime());
    }

    /**
     * 获取本月的结束时间
     *
     * @return yyyy-MM-dd HH:mm:ss  格式
     */
    public static Date getEndDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(getNowYear(), getNowMonth() - 1, 1);
        int day = calendar.getActualMaximum(5);
        calendar.set(getNowYear(), getNowMonth() - 1, day);
        return getDayEndTime(calendar.getTime());
    }

    /**
     * 获取指定月的结束时间
     *
     * @return yyyy-MM-dd HH:mm:ss  格式
     */
    public static String getMonthEnd(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        //设置为当月最后一天
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date resultDate = c.getTime();
        return sdf.format(resultDate);
    }

    /**
     * 获取指定月的开始时间
     *
     * @return yyyy-MM-dd HH:mm:ss  格式
     */
    public static String getMonthBegin(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        String string = sdf.format(date);
        return string + "-01";
    }





    /**
     * 获取近num月或将来num月的开始时间
     *
     * @return yyyy-MM-dd HH:mm:ss  格式
     */
    public static Date getBeginDayOfMonth(int num) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(getNowYear(), getNowMonth() + num, 1);
        return getDayBeginTime(calendar.getTime());
    }

    /**
     * 获取近num月或将来num月的结束时间
     *
     * @return yyyy-MM-dd HH:mm:ss  格式
     */
    public static Date getEndDayOfMonth(int num) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(getNowYear(), getNowMonth() + num, 1);
        int day = calendar.getActualMaximum(5);
        calendar.set(getNowYear(), getNowMonth() + num, day);
        return getDayEndTime(calendar.getTime());
    }

    /**
     * 获取上月的开始时间
     *
     * @return yyyy-MM-dd HH:mm:ss  格式
     */
    public static Date getBeginDayOfBeforeMonth() {
        Calendar calendar = Calendar.getInstance();//获取当前日期
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return getDayBeginTime(calendar.getTime());
    }

    /**
     * 获取上月的结束时间
     *
     * @return yyyy-MM-dd HH:mm:ss  格式
     */
    public static Date getEndDayOfBeforeMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 0);
        return getDayEndTime(calendar.getTime());
    }

    /**
     * 获取明年年的开始时间
     *
     * @return yyyy-MM-dd HH:mm:ss  格式
     */
    public static Date getBeginDayOfNextYear() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, getNowYear()+1);
        // cal.set
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DATE, 1);

        return getDayBeginTime(cal.getTime());
    }

    /**
     * 获取明年年的结束时间
     *
     * @return yyyy-MM-dd HH:mm:ss  格式
     */
    public static Date getEndDayOfNextYear() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, getNowYear()+1);
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.DATE, 31);
        return getDayEndTime(cal.getTime());
    }

    /**
     * 获取本年的开始时间
     *
     * @return yyyy-MM-dd HH:mm:ss  格式
     */
    public static Date getBeginDayOfYear() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, getNowYear());
        // cal.set
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DATE, 1);

        return getDayBeginTime(cal.getTime());
    }

    /**
     * 获取本年的结束时间
     *
     * @return yyyy-MM-dd HH:mm:ss  格式
     */
    public static Date getEndDayOfYear() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, getNowYear());
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.DATE, 31);
        return getDayEndTime(cal.getTime());
    }

    /**
     * 获取一年前的开始时间
     *
     * @return yyyy-MM-dd HH:mm:ss  格式
     */
    public static Date getBeginDayOfOneYearAgo() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getBeginDayOfTomorrow());
        cal.set(Calendar.YEAR, getNowYear() - 1);
        return getDayBeginTime(cal.getTime());
    }

    /**
     * 获取一年后的开始时间
     *
     * @return yyyy-MM-dd HH:mm:ss  格式
     */
    public static Date getBeginDayOfNextOneYear() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getBeginDayOfTomorrow());
        cal.set(Calendar.YEAR, getNowYear() + 1);
        return getDayBeginTime(cal.getTime());
    }

    /**
     * 获取近num年或后num的开始时间
     * @param num 年数 0：表示当年
     * @return yyyy-MM-dd HH:mm:ss  格式
     */
    public static Date getBeginDayOfYear(int num) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getBeginDayOfTomorrow());
        cal.set(Calendar.YEAR, getNowYear() + num);
        return getDayBeginTime(cal.getTime());
    }

    /**
     * 获取近num年或后num的结束时间
     * @param num 年数 0：表示当年
     * @return yyyy-MM-dd HH:mm:ss  格式
     */
    public static Date getEndDayOfYear(int num) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getBeginDayOfTomorrow());
        cal.set(Calendar.YEAR, getNowYear() + num);
        return getDayEndTime(cal.getTime());
    }

    /**
     * 获取某个日期的开始时间
     *
     * @param d
     * @return yyyy-MM-dd HH:mm:ss  格式
     */
    public static Timestamp getDayBeginTime(Date d) {
        Calendar calendar = Calendar.getInstance();
        if (null != d) {
            calendar.setTime(d);
        }
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return new Timestamp(calendar.getTimeInMillis());
    }

    /**
     * 获取某个日期所属月份的第一天的开始时间
     *
     * @param d
     * @return yyyy-MM-dd HH:mm:ss  格式
     */
    public static Timestamp getDayOfMonthBeginTime(Date d) {
        Calendar calendar = Calendar.getInstance();
        if (null != d) {
            calendar.setTime(d);
        }
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                1, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return new Timestamp(calendar.getTimeInMillis());
    }

    /**
     * 获取某个日期所属月份的下一月的第一天的开始时间
     *
     * @param d
     * @return yyyy-MM-dd HH:mm:ss  格式
     */
    public static Timestamp getDayOfNextMonthBeginTime(Date d) {
        Calendar calendar = Calendar.getInstance();
        if (null != d) {
            calendar.setTime(d);
        }
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                1, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return new Timestamp(calendar.getTimeInMillis());
    }

    /**
     * 获取某个日期所属年份的第一天的开始时间
     *
     * @param d
     * @return yyyy-MM-dd HH:mm:ss  格式
     */
    public static Timestamp getDayOfYearBeginTime(Date d) {
        Calendar calendar = Calendar.getInstance();
        if (null != d) {
            calendar.setTime(d);
        }
        calendar.set(calendar.get(Calendar.YEAR), 0,
                1, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return new Timestamp(calendar.getTimeInMillis());
    }

    /**
     * 获取某个日期所属年份的下一年的第一天的开始时间
     *
     * @param d
     * @return yyyy-MM-dd HH:mm:ss  格式
     */
    public static Timestamp getDayOfNextYearBeginTime(Date d) {
        Calendar calendar = Calendar.getInstance();
        if (null != d) {
            calendar.setTime(d);
        }
        calendar.set(calendar.get(Calendar.YEAR)+1, 0,
                1, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return new Timestamp(calendar.getTimeInMillis());
    }

    /**
     * 获取某个日期的结束时间
     *
     * @param d
     * @return yyyy-MM-dd HH:mm:ss  格式
     */
    public static Timestamp getDayEndTime(Date d) {
        Calendar calendar = Calendar.getInstance();
        if (null != d) {
            calendar.setTime(d);
        }
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return new Timestamp(calendar.getTimeInMillis());
    }

    /**
     * 获取某年某月的第一天
     *
     * @param year
     * @param month
     * @return
     */
    public static Date getBeginMonthDate(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1);
        return calendar.getTime();
    }

    /**
     * 获取某年某月的最后一天
     *
     * @param year
     * @param month
     * @return
     */
    public static Date getEndMonthDate(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1);
        int day = calendar.getActualMaximum(5);
        calendar.set(year, month - 1, day);
        return calendar.getTime();
    }

    /**
     * 获取今年是哪一年
     *
     * @return
     */
    public static Integer getNowYear() {
        Date date = new Date();
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(date);
        return gc.get(Calendar.YEAR);
    }

    /**
     * 获取指定时间的年
     *
     * @return
     */
    public static Integer getYear(Date date) {
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(date);
        return gc.get(Calendar.YEAR);
    }

    /**
     * 获取本月是哪一月
     *
     * @return
     */
    public static int getNowMonth() {
        Date date = new Date();
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(date);
        return gc.get(Calendar.MONTH) + 1;
    }

    /**
     * 获取指定时间的月
     *
     * @return
     */
    public static int getMonth(Date date) {
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(date);
        return gc.get(Calendar.MONTH) + 1;
    }

    /**
     * 两个日期相减得到的天数
     *
     * @param beginDate
     * @param endDate
     * @return
     */
    public static int getDiffDays(Date beginDate, Date endDate) {

        if (beginDate == null || endDate == null) {
            throw new IllegalArgumentException("getDiffDays param is null!");
        }

        Long diff = (endDate.getTime() - beginDate.getTime())
                / (1000 * 60 * 60 * 24);

        return diff.intValue();
    }

    /**
     * 两个日期相减得到的毫秒数
     *
     * @param beginDate
     * @param endDate
     * @return
     */
    public static long dateDiff(Date beginDate, Date endDate) {
        long date1ms = beginDate.getTime();
        long date2ms = endDate.getTime();
        return date2ms - date1ms;
    }

    /**
     * 获取两个日期中的最大日期
     *
     * @param beginDate
     * @param endDate
     * @return
     */
    public static Date max(Date beginDate, Date endDate) {
        if (beginDate == null) {
            return endDate;
        }
        if (endDate == null) {
            return beginDate;
        }
        if (beginDate.after(endDate)) {
            return beginDate;
        }
        return endDate;
    }

    /**
     * 获取两个日期中的最小日期
     *
     * @param beginDate
     * @param endDate
     * @return
     */
    public static Date min(Date beginDate, Date endDate) {
        if (beginDate == null) {
            return endDate;
        }
        if (endDate == null) {
            return beginDate;
        }
        if (beginDate.after(endDate)) {
            return endDate;
        }
        return beginDate;
    }

    /**
     * 返回某月该季度的第一个月
     *
     * @param date
     * @return
     */
    public static Date getFirstSeasonDate(Date date) {
        final int[] season = {1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int sean = season[cal.get(Calendar.MONTH)];
        cal.set(Calendar.MONTH, sean * 3 - 3);
        return cal.getTime();
    }

    /**
     * 返回某个日期下几天的日期
     *
     * @param date
     * @param i
     * @return
     */
    public static Date getNextDay(Date date, int i) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.set(Calendar.DATE, cal.get(Calendar.DATE) + i);
        return cal.getTime();
    }

    /**
     * 返回某个日期前几天的日期
     *
     * @param date
     * @param i
     * @return
     */
    public static Date getFrontDay(Date date, int i) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.set(Calendar.DATE, cal.get(Calendar.DATE) - i);
        return cal.getTime();
    }

    /**
     * 获取某年某月到某年某月按天的切片日期集合（间隔天数的日期集合）
     *
     * @param beginYear
     * @param beginMonth
     * @param endYear
     * @param endMonth
     * @param k
     * @return
     */
    public static List<List<Date>> getTimeList(int beginYear, int beginMonth, int endYear,
                                               int endMonth, int k) {
        List<List<Date>> list = new ArrayList<>();
        if (beginYear == endYear) {
            for (int j = beginMonth; j <= endMonth; j++) {
                list.add(getTimeList(beginYear, j, k));

            }
        } else {
            for (int j = beginMonth; j < 12; j++) {
                list.add(getTimeList(beginYear, j, k));
            }

            for (int i = beginYear + 1; i < endYear; i++) {
                for (int j = 0; j < 12; j++) {
                    list.add(getTimeList(i, j, k));
                }
            }
            for (int j = 0; j <= endMonth; j++) {
                list.add(getTimeList(endYear, j, k));
            }
        }
        return list;
    }

    /**
     * 获取某年某月按天切片日期集合（某个月间隔多少天的日期集合）
     *
     * @param beginYear 开始年
     * @param beginMonth 开始月
     * @param k 间隔时间
     * @return
     */
    public static List<Date> getTimeList(int beginYear, int beginMonth, int k) {
        List<Date> list = new ArrayList<>();
        Calendar begincal = new GregorianCalendar(beginYear, beginMonth-1, 1);
        int max = begincal.getActualMaximum(Calendar.DATE);
        for (int i = 1; i < max; i = i + k) {
            list.add(begincal.getTime());
            begincal.add(Calendar.DATE, k);
        }
        begincal = new GregorianCalendar(beginYear, beginMonth, max);
        list.add(begincal.getTime());
        return list;
    }

    /**
     * 获取某年某月按小时切片日期集合（某个月间隔多少天的日期集合）
     *
     * @param beginYear 开始年
     * @param beginMonth 开始月
     * @param k 间隔时间
     * @return
     */
    public static List<Date> getTimeListByHour(int beginYear, int beginMonth, int dayOfMonth, int k) {
        List<Date> list = new ArrayList<>();
        Calendar begincal = new GregorianCalendar(beginYear, beginMonth-1, dayOfMonth);
        int max = begincal.getActualMaximum(Calendar.HOUR_OF_DAY);
        for (int i = 0; i < max; i = i + k) {
            list.add(begincal.getTime());
            begincal.add(Calendar.HOUR_OF_DAY, k);
        }
        list.add(begincal.getTime());
        return list;
    }

    /**
     * 格式化日期
     * yyyy-MM-dd HH:mm:ss.SSS
     *
     * @param  date 待格式化时间
     */
    public static String formatHaomiao(Date date) {
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return sd.format(date);
    }


    /**
     * 格式化日期
     * yyyy-MM-dd HH:mm:ss
     *
     * @param date 待格式化时间
     */
    public static String format(Date date) {
        SimpleDateFormat sd = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
        return sd.format(date);
    }

    /**
     * 格式化日期
     * yyyy-MM-dd HH:mm:ss
     *
     *  @param  date 待格式化时间
     *  @return
     */
    public static String formatPattern(Date date, String pattern) {
        SimpleDateFormat sd = new SimpleDateFormat(pattern);
        return sd.format(date);
    }


    public static String getLastWeekTimeInterval() {
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        int dayOfWeek = calendar1.get(Calendar.DAY_OF_WEEK) - 1;
        int offset1 = 1 - dayOfWeek;
        int offset2 = 7 - dayOfWeek;
        calendar1.add(Calendar.DATE, offset1 - 7);
        calendar2.add(Calendar.DATE, offset2 - 7);
        String lastBeginDate = format(calendar1.getTime());
        String lastEndDate = format(calendar2.getTime());
        return lastBeginDate + "," + lastEndDate;
    }

    public static String getWeekTimeInterval(int num) {
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        int dayOfWeek = calendar1.get(Calendar.DAY_OF_WEEK) - 1;
        int offset1 = 1 - dayOfWeek;
        int offset2 = 7 - dayOfWeek;
        calendar1.add(Calendar.DATE, offset1 + 7 * num);
        calendar2.add(Calendar.DATE, offset2 + 7 * num);
        String lastBeginDate = format(calendar1.getTime());
        String lastEndDate = format(calendar2.getTime());
        return lastBeginDate + "," + lastEndDate;
    }

    public static List<Date> getDatesByHours(Date startTime, Date endTime, int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);
        List<Date> dates = new ArrayList<>();
        while (calendar.getTime().before(endTime)){
            dates.add(calendar.getTime());
            calendar.add(Calendar.HOUR_OF_DAY,hours);
        }
        return dates;
    }
    public static List<Date> getDatesByDays(Date startTime, Date endTime, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);
        List<Date> dates = new ArrayList<>();
        while (calendar.getTime().before(endTime)){
            dates.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH,days);
        }
        return dates;
    }

    public static List<Date> getDatesByMonths(Date startTime, Date endTime, int months) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);
        List<Date> dates = new ArrayList<>();
        while (calendar.getTime().before(endTime)){
            dates.add(calendar.getTime());
            calendar.add(Calendar.MONTH,months);
        }
        return dates;
    }

}
