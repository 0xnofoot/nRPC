package xyz.nofoot.registry.zk;

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
 * @package: xyz.nofoot.registry.zk
 * @className: ZkServiceDiscoveryImpl
 * @author: NoFoot
 * @date: 4/17/2023 6:17 PM
 * @description 服务发现的 zookeeper实现
 */
@Slf4j
public class ZkServiceDiscoveryImpl implements ServiceDiscovery {
    private final LoadBalance loadBalance;

    public ZkServiceDiscoveryImpl() {
        this.loadBalance = ExtensionLoader.getExtensionLoader(LoadBalance.class)
                .getExtension(LoadBalanceEnum.LOADBALANCE.getName());
    }

    @Override
    public InetSocketAddress lookupService(RpcRequest rpcRequest) {
        String rpcServiceName = rpcRequest.getRpcServiceName();
        CuratorFramework zkClient = CuratorUtil.getZkClient();
        List<String> serviceUrlList = CuratorUtil.getChildrenNodes(zkClient, rpcServiceName);
        if (CollectionUtil.isEmpty(serviceUrlList)) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND, rpcServiceName);
        }

        String targetServiceUrl = loadBalance.selectServerAddress(serviceUrlList, rpcRequest);
//        log.info("Successfully found the service address:[{}]", targetServiceUrl);
        String[] socketAddressArray = targetServiceUrl.split(":");
        String host = socketAddressArray[0];
        int port = Integer.parseInt(socketAddressArray[1]);

        return new InetSocketAddress(host, port);
    }
}
