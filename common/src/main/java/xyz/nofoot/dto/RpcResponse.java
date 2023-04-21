package xyz.nofoot.dto;

import lombok.*;
import xyz.nofoot.enums.RpcResponseCodeEnum;

import java.io.Serial;
import java.io.Serializable;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.dto
 * @className: RpcResponse
 * @author: NoFoot
 * @date: 4/17/2023 1:05 PM
 * @description: RPC response 实体类
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RpcResponse<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 846298462048640197L;
    // 对应的请求 id
    private String requestId;
    // 响应状态码
    private Integer code;
    // 响应消息
    private String message;
    // 封装的具体数据
    private T data;

    /**
     * @param data:
     * @param requestId:
     * @return: RpcResponse<T>
     * @author: NoFoot
     * @date: 4/17/2023 1:08 PM
     * @description: 返回一个成功的 response 消息体
     */
    public static <T> RpcResponse<T> success(T data, String requestId) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setCode(RpcResponseCodeEnum.SUCCESS.getCode());
        response.setMessage(RpcResponseCodeEnum.SUCCESS.getMessage());
        response.setRequestId(requestId);
        if (null != data) {
            response.setData(data);
        }
        return response;
    }

    /**
     * @param rpcResponseCodeEnum:
     * @return: RpcResponse<T>
     * @author: NoFoot
     * @date: 4/17/2023 1:19 PM
     * @description: 返回一个失败的 response 消息体，枚举变量可自定义传入, 或传入 FAIL
     */
    public static <T> RpcResponse<T> fail(RpcResponseCodeEnum rpcResponseCodeEnum) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setCode(rpcResponseCodeEnum.getCode());
        response.setMessage(rpcResponseCodeEnum.getMessage());
        return response;
    }
}
