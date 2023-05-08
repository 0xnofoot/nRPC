package xyz.nofoot.utils.threadPool;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.*;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.utils.threadPool
 * @className: ThreadPoolFactoryUtil
 * @author: NoFoot
 * @date: 4/21/2023 11:11 AM
 * @description: TODO
 */
@Slf4j
public class ThreadPoolFactoryUtil {

    private static final Map<String, ExecutorService> THREAD_POOLS = new ConcurrentHashMap<>();

    /**
     * @return: null
     * @author: NoFoot
     * @date: 4/21/2023 11:13 AM
     * @description: TODO
     */
    private ThreadPoolFactoryUtil() {

    }

    /**
     * @param threadNamePrefix:
     * @return: ExecutorService
     * @author: NoFoot
     * @date: 4/21/2023 12:39 PM
     * @description: TODO
     */
    public static ExecutorService createCustomThreadPoolIfAbsent(String threadNamePrefix) {
        ThreadPoolConfig threadPoolConfig = new ThreadPoolConfig();
        return createCustomThreadPoolIfAbsent(threadPoolConfig, threadNamePrefix, false);
    }

    /**
     * @param threadNamePrefix:
     * @param threadPoolConfig:
     * @return: ExecutorService
     * @author: NoFoot
     * @date: 4/21/2023 1:06 PM
     * @description: TODO
     */
    public static ExecutorService createCustomThreadPoolIfAbsent(String threadNamePrefix, ThreadPoolConfig threadPoolConfig) {
        return createCustomThreadPoolIfAbsent(threadPoolConfig, threadNamePrefix, false);
    }

    /**
     * @param threadPoolConfig:
     * @param threadNamePrefix:
     * @param daemon:
     * @return: ExecutorService
     * @author: NoFoot
     * @date: 4/21/2023 12:39 PM
     * @description: TODO
     */
    private static ExecutorService createCustomThreadPoolIfAbsent(ThreadPoolConfig threadPoolConfig, String threadNamePrefix, boolean daemon) {
        ExecutorService threadPool = THREAD_POOLS.computeIfAbsent(threadNamePrefix
                , k -> createThreadPool(threadPoolConfig, threadNamePrefix, daemon));
        if (threadPool.isShutdown() || threadPool.isTerminated()) {
            THREAD_POOLS.remove(threadNamePrefix);
            threadPool = createThreadPool(threadPoolConfig, threadNamePrefix, daemon);
            THREAD_POOLS.put(threadNamePrefix, threadPool);
        }
        return threadPool;
    }

    /**
     * @return: void
     * @author: NoFoot
     * @date: 5/8/2023 6:05 PM
     * @description: TODO
     */
    public static void shutdownAllThreadPool() {
        log.debug("准备关闭所有线程池");
        THREAD_POOLS.entrySet().parallelStream().forEach(
                entry -> {
                    ExecutorService executorService = entry.getValue();
                    executorService.shutdown();
                    log.debug("关闭线程池：[{}] [{}]", entry.getKey(), executorService.isTerminated());
                    try {
                        if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                            log.error("线程池关闭超时, 尝试强制关闭");
                            executorService.shutdownNow();
                        }
                    } catch (InterruptedException e) {
                        log.error("线程池未正常关闭, 尝试强制关闭");
                        executorService.shutdownNow();
                    }
                }
        );
    }

    /**
     * @param threadPoolConfig:
     * @param threadNamePrefix:
     * @param daemon:
     * @return: ExecutorService
     * @author: NoFoot
     * @date: 4/21/2023 12:39 PM
     * @description: TODO
     */
    private static ExecutorService createThreadPool(ThreadPoolConfig threadPoolConfig, String threadNamePrefix, boolean daemon) {
        ThreadFactory threadFactory = createThreadFactory(threadNamePrefix, daemon);
        return new ThreadPoolExecutor(
                threadPoolConfig.getCorePoolSize(), threadPoolConfig.getMaximumPoolSize()
                , threadPoolConfig.getKeepAliveTime(), threadPoolConfig.getUnit()
                , threadPoolConfig.getWorkQueue(), threadFactory);
    }

    /**
     * @param threadNamePrefix:
     * @param daemon:
     * @return: ThreadFactory
     * @author: NoFoot
     * @date: 4/21/2023 12:39 PM
     * @description: TODO
     */
    public static ThreadFactory createThreadFactory(String threadNamePrefix, Boolean daemon) {
        if (null != threadNamePrefix) {
            if (null != daemon) {
                return new ThreadFactoryBuilder()
                        .setNameFormat(threadNamePrefix + "-%d")
                        .setDaemon(daemon).build();
            } else {
                return new ThreadFactoryBuilder()
                        .setNameFormat(threadNamePrefix + "-%d").build();
            }
        }
        return Executors.defaultThreadFactory();
    }

}
