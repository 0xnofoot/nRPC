package xyz.nofoot.registry;

import lombok.extern.slf4j.Slf4j;
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
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import static xyz.nofoot.constants.RpcServiceConstants.DEFAULT_PORT;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.registry
 * @className: ServiceProvider
 * @author: NoFoot
 * @date: 4/18/23 10:57 PM
 * @description: TODO
 */
@Slf4j
public class ServiceProvider {
    private final Map<String, Object> serviceMap;
    private final ServiceRegistry serviceRegistry;

    public ServiceProvider() {
        this.serviceMap = new ConcurrentHashMap<>();
        this.serviceRegistry = ExtensionLoader.getExtensionLoader(ServiceRegistry.class)
                .getExtension(ServiceRegistryEnum.ZK.getName());
    }

    /**
     * @param rpcServiceConfig:
     * @author: NoFoot
     * @date: 4/18/23 11:00 PM
     * @description: TODO
     */
    void addService(RpcServiceConfig rpcServiceConfig) {
        String rpcServiceName = rpcServiceConfig.getRpcServiceName();
        if (serviceMap.containsKey(rpcServiceName)) {
            log.warn("存在重复服务[{}]", rpcServiceName);
            return;
        }
        serviceMap.put(rpcServiceName, rpcServiceConfig.getService());
        log.info("添加服务服务[{}], 服务实现接口[{}]"
                , rpcServiceName, rpcServiceConfig.getService().getClass().getInterfaces());
    }

    /**
     * @param rpcServiceName:
     * @return: Object
     * @author: NoFoot
     * @date: 4/18/23 11:09 PM
     * @description: TODO
     */
    Object getService(String rpcServiceName) {
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
     * @description: TODO
     */
    void publishService(RpcServiceConfig rpcServiceConfig) {
        int port = DEFAULT_PORT;
        Properties properties = PropertiesFileUtil.readPropertiesFile(PropertiesKeyEnum.RPC_CONFIG_PATH.getKey());
        if (null != properties && null != properties.getProperty(PropertiesKeyEnum.PORT.getKey())) {
            String p = properties.getProperty(PropertiesKeyEnum.PORT.getKey());
            port = Integer.parseInt(p);
        }

        try {
            String host = InetAddress.getLocalHost().getHostAddress();
            this.addService(rpcServiceConfig);
            serviceRegistry.registerService(rpcServiceConfig.getRpcServiceName()
                    , new InetSocketAddress(host, port));
        } catch (UnknownHostException e) {
            log.error("服务发布失败[{}]", rpcServiceConfig.getRpcServiceName());
        }
    }
}
