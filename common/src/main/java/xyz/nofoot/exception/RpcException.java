package xyz.nofoot.exception;

import xyz.nofoot.enums.RpcErrorMessageEnum;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.exception
 * @className: RpcException
 * @author: NoFoot
 * @date: 4/18/2023 10:39 AM
 * @description: Rpc相关的异常
 */
public class RpcException extends RuntimeException {
    public RpcException(RpcErrorMessageEnum rpcErrorMessageEnum, String detail) {
        super(rpcErrorMessageEnum.getMessage() + ":" + detail);
    }

    public RpcException(RpcErrorMessageEnum rpcErrorMessageEnum) {
        super(rpcErrorMessageEnum.getMessage());
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }
}
