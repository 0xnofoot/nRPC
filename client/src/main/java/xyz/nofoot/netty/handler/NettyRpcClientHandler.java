package xyz.nofoot.netty.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import xyz.nofoot.constants.RpcConstants;
import xyz.nofoot.dto.RpcMessage;
import xyz.nofoot.dto.RpcResponse;
import xyz.nofoot.enums.CompressTypeEnum;
import xyz.nofoot.enums.SerializationTypeEnum;
import xyz.nofoot.netty.NettyRpcClient;
import xyz.nofoot.netty.UnprocessedRequests;
import xyz.nofoot.utils.SingletonFactoryUtil;

import java.net.InetSocketAddress;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.netty.handler
 * @className: NettyRpcClientHandler
 * @author: NoFoot
 * @date: 4/19/2023 5:59 PM
 * @description: client 端处理器
 */
@Slf4j
public class NettyRpcClientHandler extends ChannelInboundHandlerAdapter {
    private final UnprocessedRequests unprocessedRequests;
    private final NettyRpcClient nettyRpcClient;

    /**
     * @return: null
     * @author: NoFoot
     * @date: 4/20/2023 10:34 AM
     * @description: TODO
     */
    public NettyRpcClientHandler() {
        unprocessedRequests = SingletonFactoryUtil.getInstance(UnprocessedRequests.class);
        nettyRpcClient = SingletonFactoryUtil.getInstance(NettyRpcClient.class);
    }

    /**
     * @param ctx:
     * @param msg:
     * @return: void
     * @author: NoFoot
     * @date: 4/20/2023 10:36 AM
     * @description: 入站消息处理，根据消息类型做操作
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            log.info("Client 收到消息：[{}]", msg);
            if (msg instanceof RpcMessage tmp) {
                byte messageType = tmp.getMessageType();
                if (messageType == RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
                    log.info("心跳检测 [{}]", tmp.getData());
                } else if (messageType == RpcConstants.RESPONSE_TYPE) {
                    RpcResponse<Object> rpcResponse = (RpcResponse<Object>) tmp.getData();
                    unprocessedRequests.complete(rpcResponse);
                }
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }


    /**
     * @param ctx:
     * @param evt:
     * @return: void
     * @author: NoFoot
     * @date: 4/20/2023 10:36 AM
     * @description: 中断处理，发送一个ping包到Server端
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent idleStateEvent) {
            IdleState state = idleStateEvent.state();
            if (state == IdleState.WRITER_IDLE) {
                log.info("write idle 发生 [{}]", ctx.channel().remoteAddress());
                Channel channel = nettyRpcClient.getChannel((InetSocketAddress) ctx.channel().remoteAddress());
                RpcMessage rpcMessage = new RpcMessage();
                // TODO 改 配置文件
                rpcMessage.setCodec(SerializationTypeEnum.PROTOSTUFF.getCode());
                rpcMessage.setCompress(CompressTypeEnum.GZIP.getCode());
                rpcMessage.setMessageType(RpcConstants.HEARTBEAT_REQUEST_TYPE);
                rpcMessage.setData(RpcConstants.PING);
                channel.writeAndFlush(rpcMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            } else {
                super.userEventTriggered(ctx, evt);
            }
        }
    }

    /**
     * @param ctx:
     * @param cause:
     * @return: void
     * @author: NoFoot
     * @date: 4/20/2023 10:36 AM
     * @description: TODO
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Client 异常: [{}]", cause.toString());
        ctx.close();
    }
}
