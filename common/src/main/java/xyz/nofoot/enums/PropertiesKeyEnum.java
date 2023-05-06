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
    // 注册中心实现
    RPC_REGISTRY("rpc.registry"),
    // 服务发布的端口
    PORT("port"),
    // zookeeper 地址的 key
    ZK_ADDRESS("rpc.zookeeper.address"),
    // redis 地址的 key
    REDIS_ADDRESS("rpc.redis.address"),
    // redis 地址的 key
    REDIS_USERNAME("rpc.redis.username"),
    // redis 地址的 key
    REDIS_PASSWORD("rpc.redis.password");
    // 在这里自定义一些配置的 key

    private final String key;
}
