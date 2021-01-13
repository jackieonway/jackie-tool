package com.github.jackieonway.util.idgenerator;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Jackie
 * @version 1.0
 **/
public class TimeThreadLocal {

    private TimeThreadLocal(){}

    private  static final ThreadLocal<SimpleDateFormat> TIME_THREAD_LOCAL = new ThreadLocal<SimpleDateFormat>(){

        @Override
        protected synchronized SimpleDateFormat initialValue(){
            return new SimpleDateFormat("yyyyMMddHHmmssSSS");
        }
    };

    public static String getTime(){
        return TIME_THREAD_LOCAL.get().format(new Date());
    }
}
