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
 * 该注解加到类上时，会被 rpcScan 扫描装载成 bean, 不需要添加参数，其目的只是为了被扫描和标识这是一个 rpc 用的 bean
 * 该注解加到字段上时，表示该字段方法需要 rpc 调用， 自定义的 spring 后处理器会解析该字段上的信息，并为其注入代理类
 * 因此如果你需要调用 rpc 方法，请在字段上和字段所在类上都使用该注解，才会被正确解析
 * 当然其实只要字段所在类被 spring 装载了就行，用 component 或者你自定义的扫描注解都行，
 * 但是字段必须正确使用该注解, 字段声明的接口和该注解中的参数唯一标识了 rpc 服务的坐标
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
