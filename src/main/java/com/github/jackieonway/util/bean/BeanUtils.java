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

    private static final Logger logger = LoggerFactory.getLogger(CustomBeanCopier.class);
    private static final Map<String, CustomBeanCopier> BEAN_COPIER_CACHE_MAP = new ConcurrentHashMap<>();
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
     * @param r 数据（对象）
     * @param clazz 目标类
     * @description  转换实体（默认字段）浅复制
     * @date 2021/1/14 9:54
     * @return 目标类
     */
    public static <T, R> T copyProperties(R r, Class<T> clazz) {
        T target = null;
        try {
            target = clazz.newInstance();
        } catch (Exception e) {
            logger.error("克隆异常", e);
        }
        String cacheKey = r.getClass().toString() + target.getClass().toString();
        CustomBeanCopier beanCopier;
        // 线程1和线程2，同时过来了
        if (!BEAN_COPIER_CACHE_MAP.containsKey(cacheKey)) {
            // 两个线程都卡这儿了
            // 但是此时线程1先获取到了锁，线程2就等着
            synchronized (CustomBeanCopier.class) {
                // 线程1进来之后，发现这里还是没有那个BeanCopier实例
                // 此时线程2，会发现缓存map中已经有了那个BeanCopier实例了，此时就不会进入if判断内的代码
                if (!BEAN_COPIER_CACHE_MAP.containsKey(cacheKey)) {
                    // 进入到这里会创建一个BeanCopier实例并且放在缓存map中
                    beanCopier = CustomBeanCopier.create(r.getClass(), target.getClass(), false);
                    BEAN_COPIER_CACHE_MAP.put(cacheKey, beanCopier);
                } else {
                    beanCopier = BEAN_COPIER_CACHE_MAP.get(cacheKey);
                }
            }
        } else {
            beanCopier = BEAN_COPIER_CACHE_MAP.get(cacheKey);
        }
        beanCopier.copy(r, target);
        return target;
    }
    /**
     * 转换实体（默认字段）
     *
     * @param source    数据（对象）
     * @param targetClass 目标类
     * @return 目标类对象
     */
    public static  <E, T> E deepCopyProperties(T source, Class<E> targetClass) {
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
    public static <E, T> List<E> deepCopyProperties(Collection<T> source, Class<E> targetClass) {
        return MAPPER_FACADE.mapAsList(source, targetClass);
    }
    /**
     * 转换集合（默认字段）
     * 浅拷贝
     * @param source    数据（集合）
     * @param targetClass 目标类
     * @return 目标类对象集合
     */
    public static <E, T> List<E> copyProperties(Collection<T> source, Class<E> targetClass) {
        if (source == null|| source.isEmpty()){
            return new ArrayList<>();
        }
        return source.stream().map(e->copyProperties(e, targetClass)).collect(Collectors.toList());
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