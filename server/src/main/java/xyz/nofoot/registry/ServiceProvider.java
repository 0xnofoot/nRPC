package xyz.nofoot.registry;

import lombok.extern.slf4j.Slf4j;
import xyz.nofoot.config.RpcServerConfig;
import xyz.nofoot.config.RpcServiceConfig;
import xyz.nofoot.enums.PropertiesKeyEnum;
import xyz.nofoot.enums.RpcErrorMessageEnum;
import xyz.nofoot.enums.ServiceRegistryEnum;
import xyz.nofoot.exception.RpcException;
import xyz.nofoot.extension.ExtensionLoader;
import xyz.nofoot.utils.PropertiesFileUtil;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.registry
 * @className: ServiceProvider
 * @author: NoFoot
 * @date: 4/18/23 10:57 PM
 * @description: 提供服务相关方法
 */
@Slf4j
public class ServiceProvider {
    private final Map<String, Object> serviceMap;
    private final ServiceRegistry serviceRegistry;

    /**
     * @return: null
     * @author: NoFoot
     * @date: 4/19/2023 4:48 PM
     * @description: 构造
     */
    public ServiceProvider() {
        this.serviceMap = new ConcurrentHashMap<>();
        String registry = PropertiesFileUtil.getRpcProperty(PropertiesKeyEnum.RPC_REGISTRY.getKey(), ServiceRegistryEnum.ZK.getName());
        this.serviceRegistry = ExtensionLoader.getExtensionLoader(ServiceRegistry.class)
                .getExtension(registry);
    }

    /**
     * @param rpcServiceConfig:
     * @author: NoFoot
     * @date: 4/18/23 11:00 PM
     * @description: 添加服务至 Map（缓存）
     */
    public void addService(RpcServiceConfig rpcServiceConfig) {
        String rpcServiceName = rpcServiceConfig.getRpcServiceName();
        if (serviceMap.containsKey(rpcServiceName)) {
            log.warn("存在重复服务[{}]", rpcServiceName);
            return;
        }
        serviceMap.put(rpcServiceName, rpcServiceConfig.getService());
        log.info("成功添加服务[{}]", rpcServiceName);
    }

    /**
     * @param rpcServiceName:
     * @return: Object
     * @author: NoFoot
     * @date: 4/18/23 11:09 PM
     * @description: 获取服务对象
     */
    public Object getService(String rpcServiceName) {
        Object service = serviceMap.get(rpcServiceName);
        if (null == service) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND, "服务：" + rpcServiceName);
        }
        return service;
    }


    /**
     * @param rpcServiceConfig:
     * @return: void
     * @author: NoFoot
     * @date: 4/18/23 11:09 PM
     * @description: 向注册中心发布服务
     */
    public void publishService(RpcServiceConfig rpcServiceConfig) {
        int serverPort = RpcServerConfig.getServerPort();

        try {
            String host = InetAddress.getLocalHost().getHostAddress();
            this.addService(rpcServiceConfig);
            serviceRegistry.registerService(rpcServiceConfig.getRpcServiceName()
                    , new InetSocketAddress(host, serverPort));
        } catch (UnknownHostException e) {
            log.error("服务发布失败[{}]", rpcServiceConfig.getRpcServiceName());
        }
        log.info("发布服务成功[{}]", rpcServiceConfig.getRpcServiceName());
    }
}
