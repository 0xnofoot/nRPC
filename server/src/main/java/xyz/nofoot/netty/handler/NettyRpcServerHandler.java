package xyz.nofoot.netty.handler;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import xyz.nofoot.constants.RpcConstants;
import xyz.nofoot.dto.RpcMessage;
import xyz.nofoot.dto.RpcRequest;
import xyz.nofoot.dto.RpcResponse;
import xyz.nofoot.enums.CompressTypeEnum;
import xyz.nofoot.enums.RpcResponseCodeEnum;
import xyz.nofoot.enums.SerializationTypeEnum;
import xyz.nofoot.utils.SingletonFactoryUtil;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.netty.handler
 * @className: NettyRpcServerHandler
 * @author: NoFoot
 * @date: 4/21/2023 1:24 PM
 * @description: 读消息的处理类
 */
@Slf4j
public class NettyRpcServerHandler extends ChannelInboundHandlerAdapter {
    private final RpcRequestHandler rpcRequestHandler;

    /**
     * @return: null
     * @author: NoFoot
     * @date: 4/21/2023 1:38 PM
     * @description: 构造，提供 rpcRequest 的处理类
     */
    public NettyRpcServerHandler() {
        this.rpcRequestHandler = SingletonFactoryUtil.getInstance(RpcRequestHandler.class);
    }

    /**
     * @param ctx:
     * @param msg:
     * @return: void
     * @author: NoFoot
     * @date: 4/21/2023 1:38 PM
     * @description: 重写，处理读事件
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            if (msg instanceof RpcMessage rmsg) {
                log.debug("Server 接收到请求：[{}]", rmsg);
                byte messageType = rmsg.getMessageType();
                RpcMessage rpcMessage = new RpcMessage();
                rpcMessage.setCodec(SerializationTypeEnum.PROTOSTUFF.getCode());
                rpcMessage.setCompress(CompressTypeEnum.GZIP.getCode());
                ctx.writeAndFlush(rpcMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
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
     * @date: 4/21/2023 1:38 PM
     * @description: 读事件，触发后关闭对应连接
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent idleStateEvent) {
            IdleState state = idleStateEvent.state();
            if (state == IdleState.READER_IDLE) {
                log.debug("idle 读触发，关闭该连接");
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    /**
     * @param ctx:
     * @param cause:
     * @return: void
     * @author: NoFoot
     * @date: 4/21/2023 1:38 PM
     * @description: netty 异常处理
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Server 异常, 关闭该连接");
        cause.printStackTrace();
        ctx.close();
    }
}
