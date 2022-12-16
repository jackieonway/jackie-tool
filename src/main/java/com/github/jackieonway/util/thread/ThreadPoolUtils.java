package com.github.jackieonway.util.thread;

import com.github.jackieonway.util.StringUtils;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.scheduling.support.CronTrigger;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * Thread Pool Utils
 *
 * @author jackie
 * @since 1.0.3
 */
public enum ThreadPoolUtils {

    /**
     * ThreadPoolUtils instance
     */
    INSTANCE;

    private static final int CORE_POOL_SIZE = (int) (Runtime.getRuntime().availableProcessors() / 0.1);

    private static final int MAX_POOL_SIZE = CORE_POOL_SIZE << 1;

    private static final Map<String, AsyncTaskExecutor> ASYNC_TASK_EXECUTOR_MAP = new ConcurrentHashMap<>(128);

    private static final Map<String, ThreadPoolTaskScheduler> THREAD_POOL_TASK_SCHEDULER_MAP = new ConcurrentHashMap<>(128);
    private static final Map<String, ScheduledFuture<?>> STRING_SCHEDULED_FUTURE_MAP = new ConcurrentHashMap<>(128);

    private static AsyncTaskExecutor asyncTaskExecutor() {
        return asyncTaskExecutor("jackie-tool-thread");
    }

    /**
     * Async task executor
     *
     * @return {@link AsyncTaskExecutor}
     */
    public static AsyncTaskExecutor asyncTaskExecutor(String threadName) {
        if (StringUtils.isBlank(threadName)){
            throw new IllegalArgumentException("threadName param is null or empty");
        }
        AsyncTaskExecutor taskExecutor = ASYNC_TASK_EXECUTOR_MAP.get(threadName);
        if (Objects.isNull(taskExecutor)) {
            synchronized (INSTANCE) {
                taskExecutor = ASYNC_TASK_EXECUTOR_MAP.get(threadName);
                if (Objects.isNull(taskExecutor)) {
                    ThreadPoolTaskExecutor asyncTaskExecutor = new ThreadPoolTaskExecutor();
                    asyncTaskExecutor.setMaxPoolSize(MAX_POOL_SIZE);
                    asyncTaskExecutor.setCorePoolSize(CORE_POOL_SIZE);
                    asyncTaskExecutor.setThreadNamePrefix(String.format("%s-",threadName));
                    asyncTaskExecutor.setQueueCapacity(CORE_POOL_SIZE << 2);
                    asyncTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
                    asyncTaskExecutor.initialize();
                    ASYNC_TASK_EXECUTOR_MAP.put(threadName,asyncTaskExecutor);
                    taskExecutor = asyncTaskExecutor;
                }

            }
        }
        return taskExecutor;
    }

    /**
     * thread pool for execute method
     * @param runnable runnable
     * @author  Jackie
     * @since 1.0.0
     * @see ThreadPoolUtils
     */
    public static void execute(Runnable runnable){
        asyncTaskExecutor().execute(runnable);
    }

    /**
     * thread pool for execute method
     * @param threadName thread name
     * @param runnable runnable
     * @author  Jackie
     * @since 1.0.3
     * @see ThreadPoolUtils
     */
    public static void execute(Runnable runnable, String threadName){
        asyncTaskExecutor(threadName).execute(runnable);
    }

    /**
     * thread pool for submit method with runnable
     * @param runnable runnable
     * @return  result
     * @author  Jackie
     * @since 1.0.3
     * @see ThreadPoolUtils
     */
    public static Future<?> submit(Runnable runnable){
        return asyncTaskExecutor().submit(runnable);
    }

    /**
     * thread pool for submit method with runnable
     * @param threadName thread name
     * @param runnable runnable
     * @return  result
     * @author  Jackie
     * @since 1.0.3
     * @see ThreadPoolUtils
     */
    public static Future<?> submit(Runnable runnable, String threadName){
        return asyncTaskExecutor(threadName).submit(runnable);
    }

    /**
     * thread pool for submit method  with callable
     * @param callable callable
     * @return  result
     * @author  Jackie
     * @since 1.0.3
     * @see ThreadPoolUtils
     */
    public static <T> Future<T> submit(Callable<T> callable){
        return asyncTaskExecutor().submit(callable);
    }

    /**
     * thread pool for submit method  with callable
     * @param threadName thread name
     * @param callable callable
     * @return  result
     * @author  Jackie
     * @since 1.0.3
     * @see ThreadPoolUtils
     */
    public static <T> Future<T> submit(Callable<T> callable, String threadName){
        return asyncTaskExecutor(threadName).submit(callable);
    }


    /**
     * defualt schedule task executor,it's pool size is 10
     *
     * @return {@link ThreadPoolTaskScheduler}
     */
    private static ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = threadPoolTaskScheduler("jackie-tool-schedule");
        threadPoolTaskScheduler.setPoolSize(10);
        return threadPoolTaskScheduler;
    }

    public static ThreadPoolTaskScheduler threadPoolTaskScheduler(String threadName) {
        if (StringUtils.isBlank(threadName)){
            throw new IllegalArgumentException("threadName param is null or empty");
        }
        ThreadPoolTaskScheduler poolTaskScheduler = THREAD_POOL_TASK_SCHEDULER_MAP.get(threadName);
        if (Objects.isNull(poolTaskScheduler)) {
            synchronized (INSTANCE) {
                poolTaskScheduler = THREAD_POOL_TASK_SCHEDULER_MAP.get(threadName);
                if (Objects.isNull(poolTaskScheduler)) {
                    ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
                    threadPoolTaskScheduler.setThreadNamePrefix(String.format("%s-", threadName));
                    threadPoolTaskScheduler.initialize();
                    poolTaskScheduler = threadPoolTaskScheduler;
                    THREAD_POOL_TASK_SCHEDULER_MAP.put(threadName, threadPoolTaskScheduler);
                }
            }
        }
        return poolTaskScheduler;
    }


    /**
     * schedule task execute with cron expression
     * @param runnable runnable
     * @param cronExpression cron expression
     * @param scheduleThreadName schedule task name
     * @author  Jackie
     * @since 1.0.3
     * @see ThreadPoolUtils
     */
    public static void executeSchedule(Runnable runnable, String cronExpression, String scheduleThreadName){
        if (!CronExpression.isValidExpression(cronExpression)){
            throw new IllegalArgumentException(String.format("cronExpression[%s] is invalid",cronExpression));
        }
        ScheduledFuture<?> scheduledFuture = threadPoolTaskScheduler(scheduleThreadName)
                .schedule(runnable, new CronTrigger(cronExpression));
        STRING_SCHEDULED_FUTURE_MAP.put(scheduleThreadName,scheduledFuture);
    }

    /**
     * schedule task execute with delay time
     * @param runnable runnable
     * @param delay delay time
     * @param scheduleThreadName schedule task name
     * @author  Jackie
     * @since 1.0.3
     * @see ThreadPoolUtils
     */
    public static void executeDelay(Runnable runnable, Long delay, String scheduleThreadName){
        ScheduledFuture<?> scheduledFuture = threadPoolTaskScheduler(scheduleThreadName)
                .scheduleWithFixedDelay(runnable, delay);
        STRING_SCHEDULED_FUTURE_MAP.put(scheduleThreadName,scheduledFuture);
    }

    /**
     * schedule task execute with period
     * @param runnable runnable
     * @param period period
     * @param scheduleThreadName schedule task name
     * @author  Jackie
     * @since 1.0.3
     * @see ThreadPoolUtils
     */
    public static void executeRate(Runnable runnable, Long period, String scheduleThreadName){
        ScheduledFuture<?> scheduledFuture = threadPoolTaskScheduler(scheduleThreadName)
                .scheduleAtFixedRate(runnable, period);
        STRING_SCHEDULED_FUTURE_MAP.put(scheduleThreadName,scheduledFuture);
    }

    /**
     * shutdown schedule task
     * @param scheduleThreadName shutdown schedule task name
     * @author  Jackie
     * @since 1.0.3
     * @see ThreadPoolUtils
     */
    public static void shutdown(String scheduleThreadName){
        ScheduledFuture<?> scheduledFuture =
                STRING_SCHEDULED_FUTURE_MAP.containsKey(scheduleThreadName) ?
                        STRING_SCHEDULED_FUTURE_MAP.remove(scheduleThreadName) : null;
        if (Objects.nonNull(scheduledFuture)){
            scheduledFuture.cancel(Boolean.TRUE);
            ThreadPoolTaskScheduler threadPoolTaskScheduler =
                    THREAD_POOL_TASK_SCHEDULER_MAP.containsKey(scheduleThreadName) ?
                            THREAD_POOL_TASK_SCHEDULER_MAP.remove(scheduleThreadName) : null;
            if (Objects.nonNull(threadPoolTaskScheduler)){
                threadPoolTaskScheduler.shutdown();
            }
        }
    }
}
