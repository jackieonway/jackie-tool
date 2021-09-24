package com.github.jackieonway.util.bean;

import com.esotericsoftware.reflectasm.ConstructorAccess;
import com.github.jackieonway.util.collection.CollectionUtils;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Bean转换工具类
 * @author Jackie
 */
public enum BeanUtils {

    /**
     * BeanUtils 实例
     */
    INSTANCE;

    /**
     * 默认字段实例集合
     */
    private static final Map<String, MapperFacade> CACHE_MAPPER_FACADE_MAP =
            new ConcurrentHashMap<>(256);

    private static final Map<String, CustomBeanCopier> BEAN_COPIER_CACHE_MAP =
            new ConcurrentHashMap<>(256);

    private static final Object LOCK_OBJECT = new Object();

    private static final Map<String, ConstructorAccess<?>> CONSTRUCTOR_ACCESS_CACHE =
            new ConcurrentHashMap<>(256);

    /**
     * 转换实体（默认字段）
     *
     * @param source    数据（对象）
     * @param targetClass 目标类
     * @param <T> source class
     * @param <E> target class
     * @return 目标类对象
     */
    public static  <E, T> E copyProperties(T source, Class<E> targetClass) {
        return copyProperties(source, targetClass,null);
    }

    /**
     * 转换实体（自定义配置）
     *
     * @param source    数据（对象）
     * @param targetClass 目标类
     * @param configMap 自定义配置
     * @param <T> source class
     * @param <E> target class
     * @return 目标类对象
     */
    public static <E, T> E copyProperties(T source, Class<E> targetClass, Map<String, String> configMap) {
        return copyProperties(source, targetClass,configMap,null);
    }

    /**
     * 转换实体（自定义配置）
     *
     * @param source    数据（对象）
     * @param targetClass 目标类
     * @param configMap 自定义配置
     * @param excludeFields excluded field
     * @param <T> source class
     * @param <E> target class
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
     * @param <T> source class
     * @param <E> target class
     * @return 目标类对象集合
     */
    public static <E, T> List<E> copyProperties(List<T> source, Class<E> targetClass) {
        return copyProperties(source, targetClass,null);
    }

    /**
     * 转换集合（自定义配置）
     *
     * @param source    数据（集合）
     * @param targetClass 目标类
     * @param configMap 自定义配置
     * @param <T> source class
     * @param <E> target class
     * @return 目标类对象集合
     */
    public static <E, T> List<E> copyProperties(List<T> source, Class<E> targetClass,
                                             Map<String, String> configMap) {
        return copyProperties(source, targetClass, configMap,null);
    }

    /**
     * 转换集合（自定义配置）
     *
     * @param source    数据（集合）
     * @param targetClass 目标类
     * @param configMap 自定义配置
     * @param excludeFields excluded field
     * @param <T> source class
     * @param <E> target class
     * @return 目标类对象集合
     */
    public static <E, T> List<E> copyProperties(List<T> source, Class<E> targetClass,
                                                Map<String, String> configMap, List<String> excludeFields) {
        T t = source.stream().findFirst().orElseThrow(() -> new NullPointerException("映射集合，数据集合为空"));
        MapperFacade mapperFacade = getMapperFacade(t.getClass(), targetClass, configMap, excludeFields);
        return mapperFacade.mapAsList(source, targetClass);
    }

    public static <E, T> Collection<E> copyProperties(Collection<T> source, Collection<E> destination,
                                                      Class<E> targetClass) {
        return copyProperties(source, destination, targetClass, null);
    }

    public static <E, T> Collection<E> copyProperties(Collection<T> source, Collection<E> destination,
                                                      Class<E> targetClass,Map<String, String> configMap) {
        return copyProperties(source, destination, targetClass, configMap, null);
    }

    /**
     * 转换集合（自定义配置）
     *
     * @param source    数据（集合）
     * @param destination 目标集合
     * @param targetClass 目标类
     * @param configMap 自定义配置
     * @param excludeFields excluded field
     * @param <T> source class
     * @param <E> target class
     * @return 目标类对象集合
     */
    public static <E, T> Collection<E> copyProperties(Collection<T> source, Collection<E> destination,
                                     Class<E> targetClass,Map<String, String> configMap, List<String> excludeFields) {
        T t = source.stream().findFirst().orElseThrow(() -> new NullPointerException("映射集合，数据集合为空"));
        MapperFacade mapperFacade = getMapperFacade(t.getClass(), targetClass, configMap, excludeFields);
        mapperFacade.mapAsCollection(source,destination , targetClass);
        return destination;
    }


    /**
     * 转换实体（默认字段）浅复制
     * @param source 数据（对象）
     * @param targetClass 目标类
     * @param <T> source class
     * @param <E> target class
     * @return 目标类
     */
    public static <T, E> E copyPropertiesByBeanCopier(T source, Class<E> targetClass) {
        ConstructorAccess<E> constructorAccess = getConstructorAccess(targetClass);
        E target = constructorAccess.newInstance();
        String cacheKey = source.getClass().getCanonicalName() + "_" + targetClass.getCanonicalName();
        CustomBeanCopier beanCopier = BEAN_COPIER_CACHE_MAP.get(cacheKey);
        if (Objects.isNull(beanCopier)) {
            synchronized (CustomBeanCopier.class) {
                beanCopier = BEAN_COPIER_CACHE_MAP.get(cacheKey);
                if (Objects.isNull(beanCopier)) {
                    beanCopier = CustomBeanCopier.create(source.getClass(), target.getClass(), false);
                    BEAN_COPIER_CACHE_MAP.put(cacheKey, beanCopier);
                }
            }
        }
        beanCopier.copy(source, target);
        return target;
    }

    /**
     * 转换集合（默认字段）
     * 浅拷贝
     * @param source    数据（集合）
     * @param targetClass 目标类
     * @param <T> source class
     * @param <E> target class
     * @return 目标类对象集合
     */
    public static <E, T> List<E> copyPropertiesByBeanCopier(List<T> source, Class<E> targetClass) {
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
     * @param <T> source class
     * @param <E> target class
     * @return 映射类对象
     */
    private static <E, T> MapperFacade getMapperFacade(Class<T> sourceClass, Class<E> targetClass,
                                                       Map<String, String> configMap, List<String> excludes) {
        final String targetName = Objects.nonNull(targetClass.getCanonicalName()) ?
                targetClass.getCanonicalName() : targetClass.getName();
        final String sourceName = Objects.nonNull(sourceClass.getCanonicalName()) ?
                sourceClass.getCanonicalName() :
                sourceClass.getName();
        String mapKey = String.format("%s_%s", sourceName, targetName);
        MapperFacade mapperFacade = CACHE_MAPPER_FACADE_MAP.get(mapKey);
        if (Objects.nonNull(mapperFacade)) {
            return mapperFacade;
        }
        synchronized (LOCK_OBJECT){
            mapperFacade = CACHE_MAPPER_FACADE_MAP.get(mapKey);
            if (Objects.isNull(mapperFacade)){
                MapperFactory factory = new DefaultMapperFactory.Builder().build();
                ClassMapBuilder<T,E> classMapBuilder = factory.classMap(sourceClass, targetClass);
                if (CollectionUtils.isNotEmpty(configMap)){
                    configMap.forEach(classMapBuilder::field);
                }
                if (CollectionUtils.isNotEmpty(excludes)){
                    excludes.forEach(classMapBuilder::exclude);
                }
                classMapBuilder.byDefault().register();
                mapperFacade = factory.getMapperFacade();
                CACHE_MAPPER_FACADE_MAP.put(mapKey, mapperFacade);
            }
        }
        return mapperFacade;
    }

    @SuppressWarnings("unchecked")
    private static <E> ConstructorAccess<E> getConstructorAccess(Class<E> targetClass) {
        final String canonicalName = targetClass.getCanonicalName();
        ConstructorAccess<E> constructorAccess =
                (ConstructorAccess<E>) CONSTRUCTOR_ACCESS_CACHE.get(Objects.isNull(canonicalName) ?
                        targetClass.getName() : canonicalName);
        if(Objects.nonNull(constructorAccess)) {
            return constructorAccess;
        }
        try {
            constructorAccess = ConstructorAccess.get(targetClass);
            CONSTRUCTOR_ACCESS_CACHE.put(canonicalName,constructorAccess);
        } catch (Exception e) {
            throw new IllegalStateException(
                    String.format("Create new instance of %s failed: %s", targetClass, e.getMessage()),e);
        }
        return constructorAccess;
    }
}