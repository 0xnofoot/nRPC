package xyz.nofoot.annotation;

import org.springframework.context.annotation.DependsOn;

import java.lang.annotation.*;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.annotation
 * @annotationName: RpcReference
 * @author: NoFoot
 * @date: 4/18/2023 3:28 PM
 * @description: Rpc 服务自动扫描注解, 使用了该注解的字段会被 Spring 构造为代理对象
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface RpcReference {

    // 服务所属组别
    String group() default "";

    // 服务版本号
    String version() default "";
}
