package xyz.nofoot.dto;

import lombok.*;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.dto
 * @className: RpcMessage
 * @author: NoFoot
 * @date: 4/17/2023 12:52 PM
 * @description: RPC 消息的实体类
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RpcMessage {
    // 消息类型
    private byte messageType;
    // 序列化类型
    private byte codec;
    // 压缩类型
    private byte compress;
    // 消息 id
    private int messageId;
    // 封装的具体数据
    private Object data;
}
