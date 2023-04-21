package xyz.nofoot.netty;

import lombok.extern.slf4j.Slf4j;
import xyz.nofoot.constants.RpcServiceConstants;
import xyz.nofoot.utils.CuratorUtil;

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
        log.info("服务关闭，执行清理任务");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                InetSocketAddress inetSocketAddress = new InetSocketAddress(
                        InetAddress.getLocalHost().getHostAddress(), RpcServiceConstants.DEFAULT_PORT);
                CuratorUtil.clearRegistry(CuratorUtil.getZkClient(), inetSocketAddress);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

        }));
    }

}
