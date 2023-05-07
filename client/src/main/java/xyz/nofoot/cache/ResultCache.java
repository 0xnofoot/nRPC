package xyz.nofoot.cache;

import xyz.nofoot.dto.RpcRequest;
import xyz.nofoot.extension.SPI;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.cache
 * @interfaceName: ResultCache
 * @author: NoFoot
 * @date: 5/7/2023 7:53 PM
 * @description: 结果缓存接口
 * ResultCache 和 ServiceDiscovery 共用一个配置：rpc.registry, 默认是 Zk 实现
 * 也就是说，对于任何一个注册中心的实现，必须要有对应的缓存实现，尽管你可以不实现具体逻辑
 * (比如 Zk 就没有实现缓存逻辑，但是有类的声明)
 */
@SPI
public interface ResultCache {

    /**
     * @param rpcRequest:
     * @param result:
     * @return: void
     * @author: NoFoot
     * @date: 5/7/2023 8:04 PM
     * @description: TODO
     */
    default void cacheResult(RpcRequest rpcRequest, Object result) {
    }

    /**
     * @param rpcRequest:
     * @return: Object
     * @author: NoFoot
     * @date: 5/7/2023 7:50 PM
     * @description: TODO
     */
    default Object getCacheResult(RpcRequest rpcRequest) {
        return null;
    }
}
