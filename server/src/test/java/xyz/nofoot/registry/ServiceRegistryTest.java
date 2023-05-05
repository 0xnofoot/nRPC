package xyz.nofoot.registry;

import org.junit.Test;
import xyz.nofoot.config.RpcServiceConfig;
import xyz.nofoot.registry.impl.ZkServiceRegistry;

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
}
