package xyz.nofoot.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

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
    // TODO 不要实现 RpcRequestTransport 接口，直接实现具体类
//    private final RpcRequestTransport rpcClient;


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }
}
