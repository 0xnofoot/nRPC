package xyz.nofoot.dto;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.dto
 * @className: RpcRequest
 * @author: NoFoot
 * @date: 4/17/2023 12:56 PM
 * @description RPC request 实体类
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString
public class RpcRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1284998445410255416L;
    // 请求 id
    private String requestID;
    // 接口名称
    private String interfaceName;
    // 方法名称
    private String methodName;
    // 参数数组
    private Object[] parameters;
    // 参数类型
    private Class<?>[] parameterTypes;
    // 版本号
    private String version;
    // 组号
    private String group;

    /**
     * @return String
     * @author NoFoot
     * @date 4/17/2023 1:04 PM
     * @description 返回服务名称，一个具体的服务由 interfaceName，group，version三部分决定
     */
    public String getRpcServiceName() {
        return this.getInterfaceName() + this.getGroup() + this.getVersion();
    }

}