package xyz.nofoot.registry.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import xyz.nofoot.dto.RpcRequest;
import xyz.nofoot.enums.LoadBalanceEnum;
import xyz.nofoot.enums.RpcErrorMessageEnum;
import xyz.nofoot.exception.RpcException;
import xyz.nofoot.extension.ExtensionLoader;
import xyz.nofoot.loadbalance.LoadBalance;
import xyz.nofoot.registry.ServiceDiscovery;
import xyz.nofoot.utils.CollectionUtil;
import xyz.nofoot.utils.CuratorUtil;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.registry.impl
 * @className: ZkServiceDiscovery
 * @author: NoFoot
 * @date: 4/17/2023 6:17 PM
 * @description: 服务发现的 zookeeper实现
 */
@Slf4j
public class ZkServiceDiscovery implements ServiceDiscovery {
    private final LoadBalance loadBalance;

    /**
     * @return: null
     * @author: NoFoot
     * @date: 4/18/2023 1:22 PM
     * @description: 构造，加载负载均衡类
     */
    public ZkServiceDiscovery() {
        this.loadBalance = ExtensionLoader.getExtensionLoader(LoadBalance.class)
                .getExtension(LoadBalanceEnum.CONSISTENT_HASH.getName());
    }

    /**
     * @param rpcRequest:
     * @return: InetSocketAddress
     * @author: NoFoot
     * @date: 4/18/2023 1:22 PM
     * @description: 查找服务
     */
    @Override
    public InetSocketAddress lookupService(RpcRequest rpcRequest) {
        String rpcServiceName = rpcRequest.getRpcServiceName();
        CuratorFramework zkClient = CuratorUtil.getZkClient();
        List<String> serviceUrlList = CuratorUtil.getChildrenNodes(zkClient, rpcServiceName);
        if (CollectionUtil.isEmpty(serviceUrlList)) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND, rpcServiceName);
        }

        String targetServiceUrl = loadBalance.selectServerUrl(serviceUrlList, rpcRequest);
        log.info("成功获取服务地址 :[{}]", targetServiceUrl);
        String[] socketAddressArray = targetServiceUrl.split(":");
        String host = socketAddressArray[0];
        int port = Integer.parseInt(socketAddressArray[1]);

        return new InetSocketAddress(host, port);
    }
}
