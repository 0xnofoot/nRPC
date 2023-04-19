package xyz.nofoot.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import xyz.nofoot.annotation.RpcService;
import xyz.nofoot.config.RpcServiceConfig;
import xyz.nofoot.registry.ServiceProvider;
import xyz.nofoot.utils.SingletonFactoryUtil;


/**
 * @projectName: nRPC
 * @package: xyz.nofoot.spring
 * @className: CustomBeanPostProcessor
 * @author: NoFoot
 * @date: 4/18/23 10:42 PM
 * @description: 自定义 bean 后处理器
 */
@Slf4j
@Component
public class CustomBeanPostProcessor implements BeanPostProcessor {
    private final ServiceProvider serviceProvider;


    // TODO 不要实现 RpcRequestTransport 接口，直接实现具体类
//    private final RpcRequestTransport rpcClient;

    /**
     * @return: null
     * @author: NoFoot
     * @date: 4/19/23 12:37 AM
     * @description: 构造
     */
    public CustomBeanPostProcessor() {
        this.serviceProvider = SingletonFactoryUtil.getInstance(ServiceProvider.class);
    }

    /**
     * @param bean:
     * @param beanName:
     * @return: Object
     * @author: NoFoot
     * @date: 4/18/23 10:50 PM
     * @description: 自定义前处理器
     */
//    @SneakyThrows
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        rpcServiceRegister(bean);
        return bean;
    }


    /**
     * @param bean:
     * @return: void
     * @author: NoFoot
     * @date: 4/18/23 10:50 PM
     * @description: 扫描 RpcService 注解，发现类即自动注册服务
     */
    private void rpcServiceRegister(Object bean) {
        if (bean.getClass().isAnnotationPresent(RpcService.class)) {
            RpcService rpcService = bean.getClass().getAnnotation(RpcService.class);
            RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder()
                    .group(rpcService.group())
                    .version(rpcService.version())
                    .service(bean).build();
            serviceProvider.publishService(rpcServiceConfig);
        }
    }
}
