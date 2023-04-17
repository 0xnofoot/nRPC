package xyz.nofoot.registry.zk;

import lombok.extern.slf4j.Slf4j;
import xyz.nofoot.dto.RpcRequest;
import xyz.nofoot.registry.ServiceDiscovery;

import java.net.InetSocketAddress;
import java.util.Stack;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.registry.zk
 * @className: ZkServiceDiscoveryImpl
 * @author: NoFoot
 * @date: 4/17/2023 6:17 PM
 * @description 服务发现的 zookeeper实现
 */
@Slf4j
public class ZkServiceDiscoveryImpl implements ServiceDiscovery {
    // TODO Last

    @Override
    public InetSocketAddress lookupService(RpcRequest rpcRequest) {
        return null;
    }
}
