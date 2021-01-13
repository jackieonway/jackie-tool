package com.github.jackieonway.util.distributedlock;

import java.util.Collections;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import redis.clients.jedis.Jedis;

/**
 * redis分布式锁
 * @author Jackie
 * @version \$Id: RedisDistributedLock.java, v 0.1 2019-02-13 11:09 Jackie Exp $$
 */
public class RedisDistributedLock {

    /**
     * 操作成功返回的状态码
     */
    private static final String REDIS_SUCCESS_CODE   = "OK";
    private static final String END = " end ";

    /**
     * 尝试获取分布式锁(原子操作)
     * @param redisTemplate Redis客户端
     * @param lockKey 锁
     * @param lockValue 请求标识 (每次请求的值不能相同，若相同则获取同一把锁,返回true)
     * @param expireSeconds 过期时间(秒)
     * @return 是否获取成功
     */
    public static boolean tryGetLock(RedisTemplate<String, String> redisTemplate, String lockKey,
                                     String lockValue, int expireSeconds) {
        String status = redisTemplate.execute((RedisCallback<String>)  connection -> {
            Jedis jedis = (Jedis) connection.getNativeConnection();
            String script = "if redis.call('EXISTS', KEYS[1]) == 0 "
                + " then return redis.call('set', KEYS[1], ARGV[1], 'nx', 'ex', '"+ expireSeconds +"') "
                + END
                + " if redis.call('get', KEYS[1]) == ARGV[1] "
                + " then return redis.call('set', KEYS[1], ARGV[1], 'xx', 'ex', '"+ expireSeconds +"') "
                + " else return 'FAIL' "
                + END;
            Object result = jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(lockValue));
            return (String) result;
        });
        return StringUtils.equals(REDIS_SUCCESS_CODE, status);
    }

    /**
     * 释放分布式锁(原子操作)
     * @param redisTemplate Redis客户端
     * @param lockKey 锁
     * @param lockValue 请求标识
     * @return 是否释放成功
     */
    public static boolean release(RedisTemplate<String, String> redisTemplate, String lockKey,
                                  String lockValue) {
        String status = redisTemplate.execute((RedisCallback<String>) connection -> {
            Jedis jedis = (Jedis) connection.getNativeConnection();
            String script = "if redis.call('EXISTS', KEYS[1]) == 0"
                + " then return 'OK'"
                + END
                + " if redis.call('get', KEYS[1]) == ARGV[1] "
                + " then "
                + " if redis.call('del', KEYS[1]) == 1 "
                + " then return 'OK' "
                + END
                + " else return 'FAIL' "
                + END;
            Object result = jedis.eval(script, Collections.singletonList(lockKey),
                Collections.singletonList(lockValue));
            return (String) result;
        });
        return StringUtils.equals(REDIS_SUCCESS_CODE, status);
    }

}
