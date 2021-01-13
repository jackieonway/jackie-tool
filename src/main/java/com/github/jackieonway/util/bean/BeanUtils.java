package com.github.jackieonway.util.bean;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Bean转换工具类
 * @author Jackie
 */
public class BeanUtils {

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
        MapperFacade mapperFacade = getMapperFacade(source.getClass(), targetClass, configMap);
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
        MapperFacade mapperFacade = getMapperFacade(t.getClass(), targetClass, configMap);
        return mapperFacade.mapAsList(source, targetClass);
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
                                                       Map<String, String> configMap) {
        String mapKey = sourceClass.getCanonicalName() + "_" + targetClass.getCanonicalName();
        MapperFacade mapperFacade = CACHE_MAPPER_FACADE_MAP.get(mapKey);
        if (Objects.isNull(mapperFacade)) {
            MapperFactory factory = new DefaultMapperFactory.Builder().build();
            ClassMapBuilder<T,E> classMapBuilder = factory.classMap(sourceClass, targetClass);
            configMap.forEach(classMapBuilder::field);
            classMapBuilder.byDefault().register();
            mapperFacade = factory.getMapperFacade();
            CACHE_MAPPER_FACADE_MAP.put(mapKey, mapperFacade);
        }
        return mapperFacade;
    }
}