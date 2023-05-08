package xyz.nofoot.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;
import xyz.nofoot.compress.Compress;
import xyz.nofoot.constants.RpcConstants;
import xyz.nofoot.dto.RpcMessage;
import xyz.nofoot.dto.RpcRequest;
import xyz.nofoot.dto.RpcResponse;
import xyz.nofoot.enums.CompressTypeEnum;
import xyz.nofoot.enums.SerializationTypeEnum;
import xyz.nofoot.extension.ExtensionLoader;
import xyz.nofoot.serialize.Serializer;

import java.util.Arrays;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.netty.handler
 * @className: RpcMessageDecoder
 * @author: NoFoot
 * @date: 4/19/2023 5:58 PM
 * @description: RpcMessage 解码器
 */
@Slf4j
public class RpcMessageDecoder extends LengthFieldBasedFrameDecoder {

    /**
     * @return: null
     * @author: NoFoot
     * @date: 4/20/2023 4:08 PM
     * @description: 默认值构造
     */
    public RpcMessageDecoder() {
//      maxFrameLength: 常量默认值
//      lengthFieldOffset: 魔数长度（4个字节）加版本长度（1一个字节），为5
//      lengthFieldLength: int型代表消息长度，四个字节，为 4
//      lengthAdjustment: 指针修正到头位置，所以要想左修正 5(魔数) + 1(版本) + 4(消息长度), 为 -9
//      initialBytesToStrip: 不跳过，全部读取，因为要自己做校验，为 0
        this(RpcConstants.MAX_FRAME_LENGTH, 5, 4, -9, 0);
    }

    /**
     * @param maxFrameLength:      一帧的最大长度
     * @param lengthFieldOffset:   代表消息长度的字节偏移量（在本协议中，长度段的数据中存放的是消息的总长度，并非仅实际消息体的长度）
     * @param lengthFieldLength:   代表消息长度的字节数
     * @param lengthAdjustment:    指针修正，我需要读取所有的消息（从头到尾），所以指针要向左修正到头的位置
     * @param initialBytesToStrip: 要跳过的字节数
     * @return: null
     * @author: NoFoot
     * @date: 4/20/2023 4:09 PM
     * @description: 自定义构造
     */
    public RpcMessageDecoder(int maxFrameLength, int lengthFieldOffset
            , int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

    /**
     * @param ctx:
     * @param in:
     * @return: Object
     * @author: NoFoot
     * @date: 4/20/2023 2:40 PM
     * @description: 重写 decode
     */
    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Object decoded = super.decode(ctx, in);
        if (decoded instanceof ByteBuf frame) {
            if (frame.readableBytes() >= RpcConstants.TOTAL_LENGTH) {
                try {
                    return decodeFrame(frame);
                } catch (Exception e) {
                    log.error("帧解码错误! ", e);
                    throw e;
                } finally {
                    frame.release();
                }
            }
        }
        return decoded;
    }

    /**
     * @param in:
     * @return: Object
     * @author: NoFoot
     * @date: 4/20/2023 2:50 PM
     * @description: 解码每一帧消息
     */
    private Object decodeFrame(ByteBuf in) {
        checkMagicNumber(in);
        checkVersion(in);

        int fullLength = in.readInt();
        byte messageType = in.readByte();
        byte codecType = in.readByte();
        byte compressType = in.readByte();
        int requestId = in.readInt();

        RpcMessage rpcMessage = RpcMessage.builder()
                .codec(codecType)
                .compress(compressType)
                .requestId(requestId)
                .messageType(messageType)
                .build();
        if (messageType == RpcConstants.HEARTBEAT_REQUEST_TYPE) {
            // TODO 问题
            rpcMessage.setData(RpcConstants.PING);
            return rpcMessage;
        }
        if (messageType == RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
            rpcMessage.setData(RpcConstants.PONG);
            return rpcMessage;
        }

        int bodyLength = fullLength - RpcConstants.HEAD_LENGTH;
        if (bodyLength > 0) {
            byte[] bs = new byte[bodyLength];
            in.readBytes(bs);
            String compressName = CompressTypeEnum.getName(compressType);
            Compress compress = ExtensionLoader.getExtensionLoader(Compress.class).getExtension(compressName);
            bs = compress.deCompress(bs);

            String codecName = SerializationTypeEnum.getName(codecType);
            Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class).getExtension(codecName);
            if (messageType == RpcConstants.REQUEST_TYPE) {
                RpcRequest tmpValue = serializer.deserialize(bs, RpcRequest.class);
                rpcMessage.setData(tmpValue);
            } else if (messageType == RpcConstants.RESPONSE_TYPE) {
                RpcResponse tmpValue = serializer.deserialize(bs, RpcResponse.class);
                rpcMessage.setData(tmpValue);
            }
        }
        return rpcMessage;
    }

    /**
     * @param in:
     * @return: void
     * @author: NoFoot
     * @date: 4/20/2023 2:48 PM
     * @description: 检查版本信息
     */
    private void checkVersion(ByteBuf in) {
        byte version = in.readByte();
        if (version != RpcConstants.VERSION) {
            throw new IllegalArgumentException("Rpc 协议版本未通过校验, version: " + version);
        }
    }

    /**
     * @param in:
     * @return: void
     * @author: NoFoot
     * @date: 4/20/2023 2:40 PM
     * @description: 检查魔数
     */
    private void checkMagicNumber(ByteBuf in) {
        int len = RpcConstants.MAGIC_NUMBER.length;
        byte[] tmp = new byte[len];
        in.readBytes(tmp);
        for (int i = 0; i < len; i++) {
            if (tmp[i] != RpcConstants.MAGIC_NUMBER[i]) {
                throw new IllegalArgumentException("魔数未通过校验, magic code: " + Arrays.toString(tmp));
            }
        }
    }
}
