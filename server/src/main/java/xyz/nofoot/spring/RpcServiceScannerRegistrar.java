package xyz.nofoot.spring;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import xyz.nofoot.annotation.RpcScan;

import static xyz.nofoot.constants.SpringConstants.BEAN_BASE_PACKAGE;
import static xyz.nofoot.constants.SpringConstants.RPC_PACKAGE_ATTRIBUTE_NAME;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.spring
 * @className: RpcServiceScannerRegistrar
 * @author: NoFoot
 * @date: 4/18/2023 4:24 PM
 * @description: TODO
 */
public class RpcServiceScannerRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {

    private ResourceLoader resourceLoader;

    /**
     * @param resourceLoader:
     * @return: void
     * @author: NoFoot
     * @date: 4/18/2023 3:52 PM
     * @description: TODO
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
     * @description: TODO
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
            rpcScanBasePackages = new String[]{BEAN_BASE_PACKAGE};
        }


//        new CustomScanner(registry, )

//        Classs<? extends Annotation> clazz = ExtensionLoader.getExtensionLoader(LoadBalance.class)
//                .getExtension(LoadBalanceEnum.LOADBALANCE.getName());

    }

}
