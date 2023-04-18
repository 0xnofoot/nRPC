package xyz.nofoot.registry;

import org.junit.Test;
import xyz.nofoot.demo.DemoRpcService;
import xyz.nofoot.demo.DemoRpcServiceImpl;
import xyz.nofoot.dto.RpcRequest;
import xyz.nofoot.dto.RpcResponse;
import xyz.nofoot.registry.zk.ZkServiceDiscoveryImpl;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.registry
 * @className: ServiceDiscoveryTest
 * @author: NoFoot
 * @date: 4/18/2023 10:53 AM
 * @description: 服务发现测试
 */
public class ServiceDiscoveryTest {


    @Test
    public void RegisterServiceByZk() throws InterruptedException {
        int i = 100;
        while (true) {
            RpcRequest rpcRequest = RpcRequest.builder()
                    .interfaceName(DemoRpcService.class.getName()).parameters(new Object[]{})
                    .group("test1").version("v1").build();

            ZkServiceDiscoveryImpl zkServiceDiscovery = new ZkServiceDiscoveryImpl();
            InetSocketAddress address = zkServiceDiscovery.lookupService(rpcRequest);
            Thread.sleep(1000);
        }
    }
}
