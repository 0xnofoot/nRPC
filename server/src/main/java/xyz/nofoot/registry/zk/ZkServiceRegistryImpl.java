package xyz.nofoot.registry.zk;

import org.apache.curator.framework.CuratorFramework;
import xyz.nofoot.registry.ServiceRegistry;
import xyz.nofoot.utils.CuratorUtil;

import java.net.InetSocketAddress;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.registry.zk
 * @className: ZkServiceRegistryImpl
 * @author: NoFoot
 * @date: 4/17/2023 1:52 PM
 * @description: zookeeper 实现的服务注册功能
 */
public class ZkServiceRegistryImpl implements ServiceRegistry {
    /**
     * @param rpcServiceName:    服务名称
     * @param inetSocketAddress: 服务地址
     * @author: NoFoot
     * @date: 4/17/2023 3:21 PM
     * @description: 通过 zookeeper 注册服务
     */
    @Override
    public void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress) {
        String servicePath = CuratorUtil.ZK_REGISTER_ROOT_PATH + "/" + rpcServiceName + inetSocketAddress.toString();
        CuratorFramework zkClient = CuratorUtil.getZkClient();
        CuratorUtil.createPersistentNode(zkClient, servicePath);
    }
}
