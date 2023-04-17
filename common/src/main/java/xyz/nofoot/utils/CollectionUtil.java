package xyz.nofoot.utils;

import java.util.Collection;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.utils
 * @className: CollectionUtil
 * @author: NoFoot
 * @date 4/17/23 11:55 PM
 * @description 集合工具类
 */
public class CollectionUtil {

    /**
     * @param c:
     * @return boolean
     * @author NoFoot
     * @date 4/17/23 11:57 PM
     * @description 判断集合是否为空
     */
    public static boolean isEmpty(Collection<?> c) {
        return c == null || c.isEmpty();
    }
}
