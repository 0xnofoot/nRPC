package xyz.nofoot.annotation;

import java.lang.annotation.*;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.annotation
 * @annotationName: RpcService
 * @author: NoFoot
 * @date: 4/18/2023 3:14 PM
 * @description: Rpc 服务自动扫描注解, 使用了该注解的类会被 Spring 注册成一个服务类
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface RpcService {

    // 服务所属组别
    String group() default "";

    // 服务版本号
    String version() default "";
}
