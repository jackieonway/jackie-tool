package com.github.jackieonway.util.bean;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Bean转换工具类
 * @author Jackie
 */
public class BeanUtils {

    private static final Logger logger = LoggerFactory.getLogger(BeanUtils.class);

    private static final Map<String, CustomBeanCopier> BEAN_COPIER_CACHE_MAP = new ConcurrentHashMap<>();

    private static final Object LOCK_OBJECT = new Object();

    private BeanUtils(){}

    /**
     * 默认字段工厂
     */
    private static final MapperFactory MAPPER_FACTORY = new DefaultMapperFactory.Builder().build();

    /**
     * 默认字段实例
     */
    private static final MapperFacade MAPPER_FACADE = MAPPER_FACTORY.getMapperFacade();

    /**
     * 默认字段实例集合
     */
    private static final Map<String, MapperFacade> CACHE_MAPPER_FACADE_MAP = new ConcurrentHashMap<>();

    /**
     * 转换实体（默认字段）
     *
     * @param source    数据（对象）
     * @param targetClass 目标类
     * @return 目标类对象
     */
    public static  <E, T> E copyProperties(T source, Class<E> targetClass) {
        return MAPPER_FACADE.map(source, targetClass);
    }

    /**
     * 转换实体（自定义配置）
     *
     * @param source    数据（对象）
     * @param targetClass 目标类
     * @param configMap 自定义配置
     * @return 目标类对象
     */
    public static <E, T> E copyProperties(T source, Class<E> targetClass, Map<String, String> configMap) {
        MapperFacade mapperFacade = getMapperFacade(source.getClass(), targetClass, configMap,null);
        return mapperFacade.map(source, targetClass);
    }

    /**
     * 转换实体（自定义配置）
     *
     * @param source    数据（对象）
     * @param targetClass 目标类
     * @param configMap 自定义配置
     * @param excludeFields excluded field
     * @return 目标类对象
     */
    public static <E, T> E copyProperties(T source, Class<E> targetClass, Map<String, String> configMap,
                                          List<String> excludeFields) {
        MapperFacade mapperFacade = getMapperFacade(source.getClass(), targetClass, configMap,excludeFields);
        return mapperFacade.map(source, targetClass);
    }

    /**
     * 转换集合（默认字段）
     *
     * @param source    数据（集合）
     * @param targetClass 目标类
     * @return 目标类对象集合
     */
    public static <E, T> List<E> copyProperties(Collection<T> source, Class<E> targetClass) {
        return MAPPER_FACADE.mapAsList(source, targetClass);
    }

    /**
     * 转换集合（自定义配置）
     *
     * @param source    数据（集合）
     * @param targetClass 目标类
     * @param configMap 自定义配置
     * @return 目标类对象集合
     */
    public static <E, T> List<E> copyProperties(Collection<T> source, Class<E> targetClass,
                                             Map<String, String> configMap) {
        T t = source.stream().findFirst().orElseThrow(() -> new NullPointerException("映射集合，数据集合为空"));
        MapperFacade mapperFacade = getMapperFacade(t.getClass(), targetClass, configMap, null);
        return mapperFacade.mapAsList(source, targetClass);
    }

    /**
     * 转换集合（自定义配置）
     *
     * @param source    数据（集合）
     * @param targetClass 目标类
     * @param configMap 自定义配置
     * @param excludeFields excluded field
     * @return 目标类对象集合
     */
    public static <E, T> List<E> copyProperties(Collection<T> source, Class<E> targetClass,
                                                Map<String, String> configMap, List<String> excludeFields) {
        T t = source.stream().findFirst().orElseThrow(() -> new NullPointerException("映射集合，数据集合为空"));
        MapperFacade mapperFacade = getMapperFacade(t.getClass(), targetClass, configMap, excludeFields);
        return mapperFacade.mapAsList(source, targetClass);
    }

    /**
     * 转换实体（默认字段）浅复制
     * @param source 数据（对象）
     * @param targetClass 目标类
     * @date 2021/1/14 9:54
     * @return 目标类
     */
    public static <T, E> E copyPropertiesByBeanCopier(T source, Class<E> targetClass) {
        E target;
        try {
            target = targetClass.newInstance();
        } catch (Exception e) {
            logger.error("convert bean error", e);
            throw new IllegalStateException("convert bean error");
        }
        String cacheKey = source.getClass().getCanonicalName() + "_" + targetClass.getCanonicalName();
        CustomBeanCopier beanCopier;
        if (!BEAN_COPIER_CACHE_MAP.containsKey(cacheKey)) {
            synchronized (CustomBeanCopier.class) {
                if (!BEAN_COPIER_CACHE_MAP.containsKey(cacheKey)) {
                    beanCopier = CustomBeanCopier.create(source.getClass(), target.getClass(), false);
                    BEAN_COPIER_CACHE_MAP.put(cacheKey, beanCopier);
                }
            }
        }
        beanCopier = BEAN_COPIER_CACHE_MAP.get(cacheKey);
        beanCopier.copy(source, target);
        return target;
    }

    /**
     * 转换集合（默认字段）
     * 浅拷贝
     * @param source    数据（集合）
     * @param targetClass 目标类
     * @return 目标类对象集合
     */
    public static <E, T> List<E> copyPropertiesByBeanCopier(Collection<T> source, Class<E> targetClass) {
        if (source == null|| source.isEmpty()){
            return Collections.emptyList();
        }
        return source.stream().map(e->copyPropertiesByBeanCopier(e, targetClass)).collect(Collectors.toList());
    }


    /**
     * 获取自定义映射
     *
     * @param sourceClass 数据映射类
     * @param targetClass   映射类
     * @param configMap 自定义配置
     * @return 映射类对象
     */
    private static <E, T> MapperFacade getMapperFacade(Class<T> sourceClass, Class<E> targetClass,
                                                       Map<String, String> configMap, List<String> excludes) {
        String mapKey = sourceClass.getCanonicalName() + "_" + targetClass.getCanonicalName();
        MapperFacade mapperFacade = CACHE_MAPPER_FACADE_MAP.get(mapKey);
        if (Objects.isNull(mapperFacade)) {
            synchronized (LOCK_OBJECT){
                mapperFacade = CACHE_MAPPER_FACADE_MAP.get(mapKey);
                if (Objects.isNull(mapperFacade)){
                    MapperFactory factory = new DefaultMapperFactory.Builder().build();
                    ClassMapBuilder<T,E> classMapBuilder = factory.classMap(sourceClass, targetClass);
                    configMap.forEach(classMapBuilder::field);
                    if (Objects.nonNull(excludes) && !excludes.isEmpty()){
                        excludes.forEach(classMapBuilder::exclude);
                    }
                    classMapBuilder.byDefault().register();
                    mapperFacade = factory.getMapperFacade();
                    CACHE_MAPPER_FACADE_MAP.put(mapKey, mapperFacade);
                }
            }
        }
        return mapperFacade;
    }
}