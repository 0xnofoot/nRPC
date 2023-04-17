package xyz.nofoot.registry;

import xyz.nofoot.extension.SPI;

import java.net.InetSocketAddress;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.registry
 * @interfaceName: ServiceRegistry
 * @author: NoFoot
 * @date: 4/17/2023 1:50 PM
 * @description 服务注册功能接口
 */
@SPI
public interface ServiceRegistry {

    /**
     * @param rpcServiceName:
     * @param inetSocketAddress:
     * @author NoFoot
     * @date 4/17/2023 1:56 PM
     * @description 提供服务注册
     */
    void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress);
}
