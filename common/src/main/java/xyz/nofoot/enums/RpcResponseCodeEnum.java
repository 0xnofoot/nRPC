package xyz.nofoot.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.enums
 * @enumName: RpcResponseCodeEnum
 * @author: NoFoot
 * @date: 4/17/2023 1:12 PM
 * @description RpcResponse 调用的枚举类
 */
@AllArgsConstructor
@Getter
@ToString
public enum RpcResponseCodeEnum {

    // 成功
    SUCCESS(200, "The remote call is successful"),
    // 失败
    FAIL(500, "The remote call is fail");

    // 响应状态码
    private final int code;
    // 响应消息
    private final String message;
}
