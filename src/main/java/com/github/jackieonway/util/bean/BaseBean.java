/**
 * Jackie.
 * Copyright (c)) 2019 - 2021 All Right Reserved
 */
package com.github.jackieonway.util.bean;

import com.github.jackieonway.util.collection.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

/**
 * <p>Base Bean for any beans.
 * <p>also can use: {@link BeanUtils#copyProperties(Object, Class, Map, List)}
 * <p>if want to convert for list, can use:
 * <blockquote><pre>
 *         DO.convertList(list,DTO.class, DTO.config(),DTO.excludes());
 * </pre></blockquote>
 * <p>also can use: {@link BeanUtils#copyProperties(List, Class, Map, List)}
 * @author Jackie
 * @version $id: BaseBean.java v 0.1 2021-09-01 17:05 Jackie Exp $$
 */
public interface BaseBean {

    /**
     * convert single source object to target object
     * @param clazz target class
     * @param <E> target class
     * @return  target object
     * @author  Jackie
     * @since 1.0.2
     * @see BaseBean
     */
    default <E> E convert(Class<E>  clazz){
       return BeanUtils.copyProperties(this, clazz,configMap(),excludeFields());
    }

    /**
     * convert single source object to target object
     * @param clazz target class
     * @param <E> target class
     * @param function function
     * @return  target object
     * @author  Jackie
     * @since 1.0.2
     * @see BaseBean
     */
    default <E> E convert(Class<E>  clazz, UnaryOperator<E> function){
        final E bean = convert(clazz);
        return function.apply(bean);
    }

    /**
     * custom convert for bean ,such as : name :userName;  hobby : info.\hobby
     * @return config map
     * @author  Jackie
     * @since 1.0.2
     * @see BaseBean
     */
    Map<String, String> configMap();

    /**
     * exclude fields for bean convert
     * @return exclude field list
     * @author  Jackie
     * @since 1.0
     * @see BaseBean
     */
    List<String> excludeFields();

    /**
     * convert source object list to target object list
     * @param source source
     * @param clazz target class
     * @param configMap configMap
     * @param excludeFields excludeFields
     * @param <T> source class
     * @param <E> target class
     * @return  target object
     * @author  Jackie
     * @since 1.0.2
     * @see BaseBean
     */
    static  <E,T> List<E> convert(List<T> source, Class<E> clazz,
                                  Map<String, String> configMap, List<String> excludeFields){
        return BeanUtils.copyProperties(source, clazz,configMap,excludeFields);
    }

    /**
     * convert source object list to target object list
     * @param source source
     * @param clazz target class
     * @param configMap configMap
     * @param excludeFields excludeFields
     * @param function function
     * @param <T> source class
     * @param <E> target class
     * @return  target object
     * @author  Jackie
     * @since 1.0.2
     * @see BaseBean
     */
    static  <E,T> List<E> convert(List<T> source, Class<E> clazz, Map<String, String> configMap,
                                  List<String> excludeFields, UnaryOperator<E> function){
        final List<E> beans = convert(source, clazz, configMap, excludeFields);
        if (CollectionUtils.isNotEmpty(beans)){
            beans.forEach(function::apply);
        }
        return beans;
    }

}
