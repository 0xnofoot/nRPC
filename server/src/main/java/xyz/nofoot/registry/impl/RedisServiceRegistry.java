package xyz.nofoot.registry.impl;

import xyz.nofoot.registry.ServiceRegistry;
import xyz.nofoot.utils.RedisUtil;

import java.net.InetSocketAddress;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.registry.impl
 * @className: RedisServiceRegistry
 * @author: NoFoot
 * @date: 5/5/2023 4:24 PM
 * @description: Redis 实现的服务注册功能
 */
public class RedisServiceRegistry implements ServiceRegistry {

    /**
     * @param rpcServiceName:
     * @param inetSocketAddress:
     * @return: void
     * @author: NoFoot
     * @date: 5/5/2023 4:25 PM
     * @description: 通过 Redis 注册服务
     */
    @Override
    public void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress) {
        RedisUtil.addServiceIdentity(rpcServiceName, inetSocketAddress);
    }
}
