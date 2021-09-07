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
 * <p>if want to convert bean , can use:
 *     <blockquote><pre>
 *         public class DTO implements BaseBean {
 *              <b>@Override
 *              public Map<String, String> configMap(){
 *                  return config();
 *              }
 *              
 *              public static Map<String, String> config(){
 *                  return null;
 *              }
 *              
 *              <b>@Override
 *              public List<String> excludeFields(){
 *                  return excludes();
 *              }
 *              
 *              public static excludes(){
 *                  return null;
 *              }
 *         }
 *     </pre></blockquote>
 * <p>also can use: {@link BeanUtils#copyProperties(Object, Class, Map, List)}
 * <p>
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
     * @return  target object
     * @author  Jackie
     * @date  2021/9/1 17:22
     * @since 1.0.2
     * @see BaseBean
     */
    default <E> E convert(Class<E>  clazz){
       return BeanUtils.copyProperties(this, clazz,configMap(),excludeFields());
    }

    /**
     * convert single source object to target object
     * @param clazz target class
     * @return  target object
     * @author  Jackie
     * @date  2021/9/1 17:22
     * @since 1.0.2
     * @see BaseBean
     */
    default <E> E convert(Class<E>  clazz, UnaryOperator<E> function){
        final E bean = convert(clazz);
        return function.apply(bean);
    }

    /**
     * custom convert for bean ,such as : name -> userName;  hobby -> info.hobby
     * @return config map
     * @author  Jackie
     * @date  2021/9/7 17:05
     * @since 1.0.2
     * @see BaseBean
     */
    Map<String, String> configMap();

    /**
     * exclude fields for bean convert
     * @return exclude field list
     * @author  Jackie
     * @date  2021/9/7 17:07
     * @since 1.0
     * @see BaseBean
     */
    List<String> excludeFields();

    /**
     * convert source object list to target object list
     * @param clazz target class
     * @return  target object
     * @author  Jackie
     * @date  2021/9/1 17:22
     * @since 1.0.2
     * @see BaseBean
     */
    static  <E,T> List<E> convert(List<T> source, Class<E> clazz,
                                  Map<String, String> configMap, List<String> excludeFields){
        return BeanUtils.copyProperties(source, clazz,configMap,excludeFields);
    }

    /**
     * convert source object list to target object list
     * @param clazz target class
     * @return  target object
     * @author  Jackie
     * @date  2021/9/1 17:22
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
