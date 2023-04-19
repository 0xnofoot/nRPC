package xyz.nofoot.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.utils
 * @className: SingletonFactoryUtil
 * @author: NoFoot
 * @date: 4/19/23 12:35 AM
 * @description: 单例工厂类, 只能空参构造
 */
public final class SingletonFactoryUtil {
    private static final Map<String, Object> OBJECT_MAP = new ConcurrentHashMap<>();

    /**
     * @return: null
     * @author: NoFoot
     * @date: 4/19/2023 2:56 PM
     * @description: TODO
     */
    private SingletonFactoryUtil() {
    }


    /**
     * @param c:
     * @return: T
     * @author: NoFoot
     * @date: 4/19/23 12:37 AM
     * @description: TODO
     */
    public static <T> T getInstance(Class<T> c) {
        if (null == c) {
            throw new IllegalArgumentException();
        }

        String key = c.toString();
        if (OBJECT_MAP.containsKey(key)) {
            return c.cast(OBJECT_MAP.get(key));
        } else {
            return c.cast(OBJECT_MAP.computeIfAbsent(key, k -> {
                try {
                    return c.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }));
        }
    }
}
