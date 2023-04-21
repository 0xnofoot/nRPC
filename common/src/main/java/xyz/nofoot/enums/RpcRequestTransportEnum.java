package xyz.nofoot.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.enums
 * @enumName: RpcRequestTransportEnum
 * @author: NoFoot
 * @date: 4/21/2023 10:18 AM
 * @description: TODO
 */
@AllArgsConstructor
@Getter
public enum RpcRequestTransportEnum {
    NETTY("netty");

    private final String name;
}
