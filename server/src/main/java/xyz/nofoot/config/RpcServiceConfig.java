package xyz.nofoot.config;

import lombok.*;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.config
 * @className: RpcServiceConfig
 * @author: NoFoot
 * @date: 4/17/2023 3:54 PM
 * @description Rpc服务配置类
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RpcServiceConfig {
    private String version = "";
    private String group = "";
    private Object service;

    /**
     * @return String
     * @author NoFoot
     * @date 4/17/2023 4:03 PM
     * @description 获取 rpc 服务名称
     */
    public String getRpcServiceName() {
        return this.getServiceName() + "_" + this.getGroup() + "_" + this.getVersion();
    }

    /**
     * @return String
     * @author NoFoot
     * @date 4/17/2023 4:02 PM
     * @description 获取 service 所实现接口的全限定名称
     */
    private String getServiceName() {
        return this.service.getClass().getInterfaces()[0].getCanonicalName();
    }
}
