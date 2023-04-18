package xyz.nofoot.registry;

import xyz.nofoot.dto.RpcRequest;
import xyz.nofoot.extension.SPI;

import java.net.InetSocketAddress;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.registry
 * @interfaceName: ServiceDiscovery
 * @author: NoFoot
 * @date: 4/17/2023 1:50 PM
 * @description: 服务发现接口
 */
@SPI
public interface ServiceDiscovery {

    /**
     * @param rpcRequest: rpc 请求
     * @return: InetSocketAddress: 服务地址
     * @author: NoFoot
     * @date: 4/17/2023 6:35 PM
     * @description: 查找服务
     */
    InetSocketAddress lookupService(RpcRequest rpcRequest);

}
