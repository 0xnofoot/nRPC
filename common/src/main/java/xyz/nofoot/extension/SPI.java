package xyz.nofoot.extension;

import java.lang.annotation.*;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.loader.extension
 * @annotationName: SPI
 * @author: NoFoot
 * @date: 4/17/2023 6:26 PM
 * @description: TODO
 * refer to :https://cn.dubbo.apache.org/zh-cn/overview/mannual/java-sdk/reference-manual/spi/overview/
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SPI {
}
