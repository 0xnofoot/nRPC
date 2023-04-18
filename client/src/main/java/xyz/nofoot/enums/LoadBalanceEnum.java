package xyz.nofoot.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.enums
 * @enumName: LoadBalanceEnum
 * @author: NoFoot
 * @date: 4/18/2023 10:25 AM
 * @description: 负载均衡枚举
 */
@AllArgsConstructor
@Getter
public enum LoadBalanceEnum {
    CONSISTENT_HASH("consistentHash"),
    RANDOM("random");

    private final String name;
}
