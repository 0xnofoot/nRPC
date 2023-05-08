package xyz.nofoot.netty;

import lombok.extern.slf4j.Slf4j;
import xyz.nofoot.config.RpcServerConfig;
import xyz.nofoot.enums.PropertiesKeyEnum;
import xyz.nofoot.enums.ServiceRegistryEnum;
import xyz.nofoot.utils.CuratorUtil;
import xyz.nofoot.utils.PropertiesFileUtil;
import xyz.nofoot.utils.RedisUtil;
import xyz.nofoot.utils.threadPool.ThreadPoolFactoryUtil;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.netty
 * @className: NettyShutdownHook
 * @author: NoFoot
 * @date: 4/21/2023 11:06 AM
 * @description: TODO
 */
@Slf4j
public class NettyShutdownHook {
    private static final NettyShutdownHook SHUTDOWN_HOOK = new NettyShutdownHook();

    /**
     * @return: NettyShutdownHook
     * @author: NoFoot
     * @date: 4/21/2023 11:07 AM
     * @description: TODO
     */
    public static NettyShutdownHook getShutdownHook() {
        return SHUTDOWN_HOOK;
    }

    public void clearAll() {
        log.debug("添加服务关闭Hook，用于删除所有注册的服务");
        String registry = PropertiesFileUtil.getRpcProperty(PropertiesKeyEnum.RPC_REGISTRY.getKey(), ServiceRegistryEnum.ZK.getName());
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                InetSocketAddress inetSocketAddress = new InetSocketAddress(
                        InetAddress.getLocalHost().getHostAddress(), RpcServerConfig.getServerPort());
                if (registry.equals(ServiceRegistryEnum.ZK.getName())) {
                    CuratorUtil.clearRegistry(CuratorUtil.getZkClient(), inetSocketAddress);
                } else if (registry.equals(ServiceRegistryEnum.REDIS.getName())) {
                    RedisUtil.clearRegistry(inetSocketAddress);
                }
                ThreadPoolFactoryUtil.shutdownAllThreadPool();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            log.debug("Server 已关闭");
        }));
    }

}
