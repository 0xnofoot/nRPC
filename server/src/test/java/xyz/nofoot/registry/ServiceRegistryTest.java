package xyz.nofoot.registry;

import org.junit.Test;
import xyz.nofoot.config.RpcServiceConfig;
import xyz.nofoot.registry.impl.RedisServiceRegistry;
import xyz.nofoot.registry.impl.ZkServiceRegistry;
import xyz.nofoot.utils.RedisUtil;

import java.net.InetSocketAddress;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.registry
 * @className: ServiceRegistryTest
 * @author: NoFoot
 * @date: 4/17/2023 3:39 PM
 * @description: 服务注册测试
 */
public class ServiceRegistryTest {
    @Test
    public void RegisterServiceByZk() {
        String host = "192.168.1.6";
        int port = 18084;
        for (int i = 0; i < 5; i++) {
            ZkServiceRegistry zkServiceRegistry = new ZkServiceRegistry();
            InetSocketAddress address = new InetSocketAddress(host, port);
            RpcServiceConfig demoRpcService = RpcServiceConfig.builder()
                    .group("test1").version("v1").service(new HelloServiceImpl_1()).build();
            zkServiceRegistry.registerService(demoRpcService.getRpcServiceName(), address);
            port++;
        }
    }

    @Test
    public void RegisterServiceByRedis() {
        String host = "192.168.1.6";
        int port = 18084;
        for (int i = 0; i < 5; i++) {
            RedisServiceRegistry redisServiceRegistry = new RedisServiceRegistry();
            InetSocketAddress address = new InetSocketAddress(host, port);
            RpcServiceConfig demoRpcService = RpcServiceConfig.builder()
                    .group("test2").version("v2").service(new HelloServiceImpl_1()).build();
            redisServiceRegistry.registerService(demoRpcService.getRpcServiceName(), address);
            port++;
        }
    }

    @Test
    public void RedisUtilTest() {
        String host = "192.168.1.6";
        int port = 18084;
        InetSocketAddress address = new InetSocketAddress(host, port);
        for (int i = 0; i < 5; i++) {
            RpcServiceConfig demoRpcService = RpcServiceConfig.builder()
                    .group("test" + i).version("v1").service(new HelloServiceImpl_1()).build();
            RedisUtil.addServiceIdentity(demoRpcService.getRpcServiceName(), address);

            InetSocketAddress a = new InetSocketAddress(host, port + 1);
            RedisUtil.addServiceIdentity(demoRpcService.getRpcServiceName(), a);
        }

        RedisUtil.clearRegistry(address);
        InetSocketAddress a = new InetSocketAddress(host, port + 1);
        RedisUtil.clearRegistry(a);
    }
}
