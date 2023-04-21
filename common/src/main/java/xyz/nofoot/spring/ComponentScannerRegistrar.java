package xyz.nofoot.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import xyz.nofoot.annotation.RpcScan;

import java.lang.annotation.Annotation;
import java.util.List;

import static xyz.nofoot.constants.SpringConstants.RPC_PACKAGE_ATTRIBUTE_NAME;
import static xyz.nofoot.constants.SpringConstants.SPRING_BEAN_BASE_PACKAGE;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.spring
 * @className: ComponentScannerRegistrar
 * @author: NoFoot
 * @date: 4/18/2023 3:37 PM
 * @description: 自定义 bean 扫描和加载
 */
@Slf4j
public class ComponentScannerRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {
    private ResourceLoader resourceLoader;

    /**
     * @param resourceLoader:
     * @return: void
     * @author: NoFoot
     * @date: 4/18/2023 3:52 PM
     * @description: 赋值 resourceLoader
     */
    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /**
     * @param importingClassMetadata:
     * @param registry:
     * @return: void
     * @author: NoFoot
     * @date: 4/18/2023 3:52 PM
     * @description: 自定义 bean 方法
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // 获取类上的 RpcScan 注解的属性信息
        AnnotationAttributes rpcScanAnnotationAttributes = AnnotationAttributes
                .fromMap(importingClassMetadata.getAnnotationAttributes(RpcScan.class.getName()));

        //  解析名为 RPC_PACKAGE_ATTRIBUTE_NAME 的值，返回到 String 数组中
        String[] rpcScanBasePackages = new String[0];
        if (null != rpcScanAnnotationAttributes) {
            rpcScanBasePackages = rpcScanAnnotationAttributes.getStringArray(RPC_PACKAGE_ATTRIBUTE_NAME);
        }
        // 如果没解析到值，直接传入工程基包
        if (rpcScanBasePackages.length == 0) {
            rpcScanBasePackages = new String[]{((StandardAnnotationMetadata) importingClassMetadata).getIntrospectedClass().getPackage().getName()};
        }

        //通过注解 bean 加载器加载到所有需要的注解
        // 分为 spring 和 rpc 注解
        // 这两者本质上是一样的，只是spring注解的扫描范围由 SPRING_BEAN_BASE_PACKAGE 决定
        // rpc 注解的范围由 rpcScanBasePackages  决定， 即 rpcScan 的字段值决定
        // 这样子写最初的原因是我希望 Server 和 Client 做到最大可能的分离，同时代码能够最大复用
        // 所以在配置文件中可以决定 Spring 需要加载哪些类，Server 和 Client 有自己的配置文件即可
        // 都可以 @Import 这个类，调用这个方法做到自定义加载 bean
        List<Class<? extends Annotation>> springBeanAnnotationClasses = BeanAnnotationLoader.loadSpringBeanAnnotationClasses();
        List<Class<? extends Annotation>> rpcBeanAnnotationClasses = BeanAnnotationLoader.loadRpcBeanAnnotationClasses();

        // 获取各自的扫描器并添加要扫描的注解
        CustomScanner springBeanAnnotationClassScanner = new CustomScanner(registry);
        springBeanAnnotationClassScanner.addIncludeAnnotationTypeFilters(springBeanAnnotationClasses);
        CustomScanner rpcBeanAnnotationClassScanner = new CustomScanner(registry);
        rpcBeanAnnotationClassScanner.addIncludeAnnotationTypeFilters(rpcBeanAnnotationClasses);


        // 加载 resourceLoader
        if (resourceLoader != null) {
            springBeanAnnotationClassScanner.setResourceLoader(resourceLoader);
            rpcBeanAnnotationClassScanner.setResourceLoader(resourceLoader);
        }

        // 扫描 bean
        int count;
        count = springBeanAnnotationClassScanner.scan(SPRING_BEAN_BASE_PACKAGE);
        log.info("spring 自定义扫描器扫描到的数量：[{}]", count);

        count = rpcBeanAnnotationClassScanner.scan(rpcScanBasePackages);
        log.info("rpc 自定义扫描器扫描到的数量：[{}]", count);
    }
}
