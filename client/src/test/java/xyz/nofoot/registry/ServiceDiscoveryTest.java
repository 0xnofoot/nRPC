package xyz.nofoot.registry;

import org.junit.Test;
import xyz.nofoot.dto.RpcRequest;
import xyz.nofoot.registry.impl.RedisServiceDiscovery;
import xyz.nofoot.registry.impl.ZkServiceDiscovery;

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
                    .interfaceName(HelloService.class.getName()).parameters(new Object[]{})
                    .group("test1").version("v1").build();

            ZkServiceDiscovery zkServiceDiscovery = new ZkServiceDiscovery();
            InetSocketAddress address = zkServiceDiscovery.lookupService(rpcRequest);
            Thread.sleep(1000);
        }
    }

    @Test
    public void RegisterServiceByRedis() throws InterruptedException {
        int i = 10;
        while (i-- != 0) {
            RpcRequest rpcRequest = RpcRequest.builder()
                    .interfaceName(HelloService.class.getName()).parameters(new Object[]{})
                    .group("test1").version("v1").build();

            RedisServiceDiscovery redisServiceDiscovery = new RedisServiceDiscovery();
            InetSocketAddress address = redisServiceDiscovery.lookupService(rpcRequest);
            System.out.println(address);
            Thread.sleep(200);
        }
        System.out.println("/////////////////////");
        i = 10;
        while (i-- != 0) {
            RpcRequest rpcRequest = RpcRequest.builder()
                    .interfaceName(HelloService.class.getName()).parameters(new Object[]{})
                    .group("test2").version("v1").build();

            RedisServiceDiscovery redisServiceDiscovery = new RedisServiceDiscovery();
            InetSocketAddress address = redisServiceDiscovery.lookupService(rpcRequest);
            System.out.println(address);
            Thread.sleep(200);
        }
        System.out.println("/////////////////////");
        i = 10;
        while (i-- != 0) {
            RpcRequest rpcRequest = RpcRequest.builder()
                    .interfaceName(HelloService.class.getName()).parameters(new Object[]{})
                    .group("test3").version("v1").build();

            RedisServiceDiscovery redisServiceDiscovery = new RedisServiceDiscovery();
            InetSocketAddress address = redisServiceDiscovery.lookupService(rpcRequest);
            System.out.println(address);
            Thread.sleep(200);
        }
    }
}
