package xyz.nofoot.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;
import xyz.nofoot.compress.Compress;
import xyz.nofoot.constants.RpcConstants;
import xyz.nofoot.dto.RpcMessage;
import xyz.nofoot.enums.CompressTypeEnum;
import xyz.nofoot.enums.SerializationTypeEnum;
import xyz.nofoot.extension.ExtensionLoader;
import xyz.nofoot.serialize.Serializer;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.netty.handler
 * @className: RpcMessageEncoder
 * @author: NoFoot
 * @date: 4/19/2023 5:58 PM
 * @description: RpcMessage 编码处理器
 */
@Slf4j
public class RpcMessageEncoder extends MessageToByteEncoder<RpcMessage> {
    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(0);


    /**
     * @param ctx:
     * @param rpcMessage:
     * @param out:
     * @return: void
     * @author: NoFoot
     * @date: 4/20/2023 11:13 AM
     * @description: 根据 自定义协议 编码消息
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, RpcMessage rpcMessage, ByteBuf out) {
        try {
            out.writeBytes(RpcConstants.MAGIC_NUMBER);
            out.writeByte(RpcConstants.VERSION);
            out.writerIndex(out.writerIndex() + 4);
            byte messageType = rpcMessage.getMessageType();
            out.writeByte(messageType);
            out.writeByte(rpcMessage.getCodec());
            // TODO 配置文件
            out.writeByte(rpcMessage.getCompress());
            out.writeInt(ATOMIC_INTEGER.getAndIncrement());

            byte[] bodyBytes = null;
            int fullLength = RpcConstants.HEAD_LENGTH;

            if (messageType != RpcConstants.HEARTBEAT_REQUEST_TYPE
                    && messageType != RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
                String codecName = SerializationTypeEnum.getName(rpcMessage.getCodec());
                Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class).getExtension(codecName);
                bodyBytes = serializer.serialize(rpcMessage.getData());
                String compressName = CompressTypeEnum.getName(rpcMessage.getCompress());
                Compress compress = ExtensionLoader.getExtensionLoader(Compress.class).getExtension(compressName);
                bodyBytes = compress.compress(bodyBytes);
                fullLength += bodyBytes.length;
            }
            if (null != bodyBytes) {
                out.writeBytes(bodyBytes);
            }
            int writeIndex = out.writerIndex();
            out.writerIndex(writeIndex - fullLength + RpcConstants.MAGIC_NUMBER.length + 1);
            out.writeInt(fullLength);
            out.writerIndex(writeIndex);
        } catch (Exception e) {
            log.error("编码器错误! ", e);
        }
    }
}
