package xyz.nofoot.utils;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.utils
 * @className: RuntimeUtil
 * @author: NoFoot
 * @date: 4/21/2023 1:12 PM
 * @description: TODO
 */
public class RuntimeUtil {

    /**
     * @return: int
     * @author: NoFoot
     * @date: 4/21/2023 1:13 PM
     * @description: 获取 cpu 核心数
     */
    public static int cpus() {
        return Runtime.getRuntime().availableProcessors();
    }
}
