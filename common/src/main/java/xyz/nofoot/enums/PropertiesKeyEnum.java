package xyz.nofoot.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.enums
 * @enumName: PropertiesKeyEnum
 * @author: NoFoot
 * @date: 4/17/2023 2:55 PM
 * @description: rpc 配置枚举
 */
@AllArgsConstructor
@Getter
public enum PropertiesKeyEnum {
    // 配置文件名
    RPC_CONFIG_PATH("rpc.properties"),
    // 服务发布的端口
    PORT("port"),
    // zookeeper 地址的 key
    ZK_ADDRESS("rpc.zookeeper.address");
    // 在这里自定义一些配置的 key

    private final String key;
}
