package xyz.nofoot.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import xyz.nofoot.annotation.RpcReference;
import xyz.nofoot.config.RpcServiceConfig;
import xyz.nofoot.enums.RpcRequestTransportEnum;
import xyz.nofoot.extension.ExtensionLoader;
import xyz.nofoot.proxy.RpcClientProxy;
import xyz.nofoot.transport.RpcRequestTransport;

import java.lang.reflect.Field;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.spring
 * @className: CustomBeanPostProcessor
 * @author: NoFoot
 * @date: 4/19/2023 4:53 PM
 * @description: Client 端自定义 bean 后处理器
 */
@Slf4j
@Component
public class CustomBeanPostProcessor implements BeanPostProcessor {
    private final RpcRequestTransport rpcClient;

    /**
     * @return: null
     * @author: NoFoot
     * @date: 4/21/2023 10:24 AM
     * @description: 构造 rpcClient, 目前只实现 netty 客户端
     */
    public CustomBeanPostProcessor() {
        // TODO 配置文件
        this.rpcClient = ExtensionLoader.getExtensionLoader(RpcRequestTransport.class)
                .getExtension(RpcRequestTransportEnum.NETTY.getName());
    }


    /**
     * @param bean:
     * @param beanName:
     * @return: Object
     * @author: NoFoot
     * @date: 4/21/2023 10:54 AM
     * @description: TODO
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        wireRpcReferenceProxy(bean);
        return bean;
    }

    /**
     * @param bean:
     * @return: void
     * @author: NoFoot
     * @date: 4/21/2023 10:54 AM
     * @description: TODO
     */
    private void wireRpcReferenceProxy(Object bean) {
        Class<?> targetClass = bean.getClass();
        Field[] declaredFields = targetClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            RpcReference rpcReference = declaredField.getAnnotation(RpcReference.class);
            if (null != rpcReference) {
                RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder()
                        .group(rpcReference.group())
                        .version(rpcReference.version()).build();
                RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcClient, rpcServiceConfig);
                Object clientProxy = rpcClientProxy.getProxy(declaredField.getType());
                declaredField.setAccessible(true);
                try {
                    declaredField.set(bean, clientProxy);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
