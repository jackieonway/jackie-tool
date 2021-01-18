package com.github.jackieonway.util.xml;

import com.thoughtworks.xstream.converters.basic.DateConverter;
import  com.github.jackieonway.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * XML Date Converter
 *
 * @author Jackie
 * @version 1.0
 **/
public class XStreamDateConverter extends DateConverter {

    private Logger log = LoggerFactory.getLogger(XStreamDateConverter.class);

    public XStreamDateConverter() {
        this(null, null);
    }

    /**
     * Default time format "yyyy-MM-dd HH:mm:ss“
     */
    private static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";
    /**
     * Default Time Zone GMT+8
     */
    private static final String DEFAULT_TIME_ZONE = "GMT+8";
    private SimpleDateFormat simpleDateFormat;

    /**
     * Time format character
     */
    private String format;
    /**
     * Time Zone
     */
    private String timeZone;


	public XStreamDateConverter(String format) {
   		 this(format,null);
    }

    public XStreamDateConverter(String format, String timeZone) {
        if (StringUtils.isEmpty(format)) {
            format = DEFAULT_FORMAT;
        }
        if (StringUtils.isEmpty(timeZone)) {
            timeZone = DEFAULT_TIME_ZONE;
        }
        this.format = format;
        this.timeZone = timeZone;
        simpleDateFormat = new SimpleDateFormat(format);
        log.info(" ======= format : {}, timeZone : {} ", format, timeZone);
        new DateConverter(format, null, TimeZone.getTimeZone(timeZone));
    }

    @Override
    public synchronized Object fromString(String arg0) {
        try {
            log.info(" ======= Time string data to be formatted: {}", arg0);
            return simpleDateFormat.parse(arg0);
        } catch (ParseException e) {
            log.error(" ======= Attempt to use '{}' to format '{}' failed, reason for failure:{}", format, arg0, e.getMessage());
            try {
                Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
                cal.setTimeInMillis(Long.parseLong(arg0));
                return cal.getTime();
            } catch (Exception e1) {
                log.error(" ======= Attempt to use Long to format '{}' failed, reason for failure:{}", arg0, e.getMessage());
                return null;
            }
        }
    }

    @Override
    public synchronized String toString(Object obj) {
        log.info(" ======= Time data to be converted:{}", obj);
        return simpleDateFormat.format(obj);
    }
}
