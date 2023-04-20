package xyz.nofoot.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.enums
 * @enumName: ServiceRegistryEnum
 * @author: NoFoot
 * @date: 4/18/23 11:17 PM
 * @description: TODO
 */
@AllArgsConstructor
@Getter
public enum ServiceRegistryEnum {
    ZK("zk"),
    // TODO  redis暂未实现
    REDIS("redis");

    private final String name;
}
