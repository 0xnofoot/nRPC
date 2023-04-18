package xyz.nofoot.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.enums
 * @enumName: RpcErrorMessageEnum
 * @author: NoFoot
 * @date: 4/18/2023 10:40 AM
 * @description: Rpc 错误枚举
 */
@AllArgsConstructor
@Getter
@ToString
public enum RpcErrorMessageEnum {
    CLIENT_CONNECT_SERVER_FAILURE("客户端连接服务端失败"),
    SERVICE_INVOCATION_FAILURE("服务调用失败"),
    SERVICE_CAN_NOT_BE_FOUND("没有找到指定的服务"),
    SERVICE_NOT_IMPLEMENT_ANY_INTERFACE("注册的服务没有实现任何接口"),
    REQUEST_NOT_MATCH_RESPONSE("返回结果错误！请求和返回的结果不匹配");

    private final String message;
}
