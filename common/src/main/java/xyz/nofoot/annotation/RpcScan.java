package xyz.nofoot.annotation;

import org.springframework.context.annotation.Import;
import xyz.nofoot.spring.ComponentScannerRegistrar;

import java.lang.annotation.*;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.annotation
 * @annotationName: RpcScan
 * @author: NoFoot
 * @date: 4/18/2023 3:30 PM
 * @description: Rpc 服务扫描注解
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Import(ComponentScannerRegistrar.class)
public @interface RpcScan {

    String[] basePackage();
}
