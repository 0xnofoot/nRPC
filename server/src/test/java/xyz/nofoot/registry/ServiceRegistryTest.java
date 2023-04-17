package xyz.nofoot.registry;

import org.junit.Test;
import xyz.nofoot.registry.demoRpcService.DemoRpcServiceImpl;
import xyz.nofoot.config.RpcServiceConfig;
import xyz.nofoot.registry.zk.ZkServiceRegistryImpl;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.registry
 * @className: ServiceRegistryTest
 * @author: NoFoot
 * @date: 4/17/2023 3:39 PM
 * @description TODO
 */
public class ServiceRegistryTest {
    @Test
    public void RegisterServiceByZk() throws IOException {
        ZkServiceRegistryImpl zkServiceRegistry = new ZkServiceRegistryImpl();
        InetSocketAddress address = new InetSocketAddress("127.0.0.1", 18080);
        RpcServiceConfig demoRpcService = RpcServiceConfig.builder()
                .group("test1").version("v1").service(new DemoRpcServiceImpl()).build();
        zkServiceRegistry.registerService(demoRpcService.getRpcServiceName(), address);
    }
}
