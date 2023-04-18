package xyz.nofoot.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import xyz.nofoot.annotation.RpcService;
import xyz.nofoot.registry.ServiceProvider;
import xyz.nofoot.utils.SingletonFactoryUtil;

import java.io.Serial;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.spring
 * @className: CustomBeanPostProcessor
 * @author: NoFoot
 * @date: 4/18/23 10:42 PM
 * @description: 自定义 bean 后处理器
 */
public class CustomBeanPostProcessor implements BeanPostProcessor {
    private final ServiceProvider serviceProvider;


    // TODO 不要实现 RpcRequestTransport 接口，直接实现具体类
//    private final RpcRequestTransport rpcClient;

    /**
     * @return: null
     * @author: NoFoot
     * @date: 4/19/23 12:37 AM
     * @description: TODO
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
     * @description: TODO
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }


    /**
     * @param bean:
     * @return: void
     * @author: NoFoot
     * @date: 4/18/23 10:50 PM
     * @description: TODO
     */
    private static void rpcServiceRegister(Object bean) {
        if (bean.getClass().isAnnotationPresent(RpcService.class)) {

        }
    }
}
