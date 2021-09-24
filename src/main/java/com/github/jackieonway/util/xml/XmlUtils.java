package com.github.jackieonway.util.xml;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;

import java.util.Map;

/**
 * this xml utils is based on xstream,
 * so if you are used for convert xml,
 * the bean class must have xstream annotations
 *
 * @author Jackie
 * @version 1.0
 **/
public enum XmlUtils {
    /**
     * XmlUtils 实例
     */
    INSTANCE;
    private static final String UTF_8 = "UTF-8";

    /**
     * Parsing various xml data
     *
     * @param xml   Xml data to be parsed
     * @param clazz Object class to be parsed
     * @param <T> target class
     * @return Parsed objec
     */
    @SuppressWarnings("unchecked")
    public static <T> T parseXml(String xml, Class<T> clazz) {
        XStream xStream = new XStream(
                new DomDriver(UTF_8, new XmlFriendlyNameCoder("-_", "_")));
        //Declaring the source of X Stream annotations
        xStream.processAnnotations(clazz);
        xStream.registerConverter(new XStreamDateConverter());
        xStream.alias("xml", clazz);
        return (T) xStream.fromXML(xml);
    }

    /**
     * Parsing various xml data
     *
     * @param xml   Xml data to be parsed
     * @param clazz Object class to be parsed
     * @param format Time format
     * @param <T> target class
     * @return Parsed object
     */
    @SuppressWarnings("unchecked")
    public static <T> T parseXml(String xml, Class<T> clazz, String format) {
        XStream xStream = new XStream(
                new DomDriver(UTF_8, new XmlFriendlyNameCoder("-_", "_")));
        //Declaring the source of X Stream annotations
        xStream.processAnnotations(clazz);
        xStream.registerConverter(new XStreamDateConverter(format));
        xStream.alias("xml", clazz);
        return (T) xStream.fromXML(xml);
    }

    /**
     * Object to xml string
     *
     * @param obj Object
     * @param map Alias ​​collection key- alias value-the class requiring the alias
     * @param <T> target class
     * @return xml string
     */
    public static <T> String toXml(Object obj, Map<String, Class<T>> map) {
        XStream xStream = new XStream(
                new DomDriver(UTF_8, new XmlFriendlyNameCoder("-_", "_")));
        xStream.processAnnotations(obj.getClass());
        if (map != null && !map.isEmpty()) {
            map.forEach(xStream::alias);
        }
        xStream.registerConverter(new XStreamDateConverter());
        return xStream.toXML(obj);
    }

    /**
     * Object to xml string
     *
     * @param obj Object
     * @param map Alias ​​collection key- alias value-the class requiring the alias
     * @param format Time format
     * @param <T> target class
     * @return xml string
     */
    public static <T> String toXml(Object obj, Map<String, Class<T>> map, String format) {
        XStream xStream = new XStream(
                new DomDriver(UTF_8, new XmlFriendlyNameCoder("-_", "_")));
        xStream.processAnnotations(obj.getClass());
        if (map != null && !map.isEmpty()) {
            map.forEach(xStream::alias);
        }
        xStream.registerConverter(new XStreamDateConverter(format));
        return xStream.toXML(obj);
    }
}
