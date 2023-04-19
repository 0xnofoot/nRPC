package xyz.nofoot.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import xyz.nofoot.netty.ChannelProvider;
import xyz.nofoot.netty.UnprocessedRequests;
import xyz.nofoot.netty.handler.NettyRpcClientHandler;
import xyz.nofoot.netty.handler.RpcMessageDecoder;
import xyz.nofoot.netty.handler.RpcMessageEncoder;
import xyz.nofoot.registry.ServiceDiscovery;

import java.util.concurrent.TimeUnit;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.client
 * @className: NettyRpcClient
 * @author: NoFoot
 * @date: 4/19/2023 4:56 PM
 * @description: TODO
 */
@Slf4j
public class NettyRpcClient {
    private final ServiceDiscovery serviceDiscovery;
    private final UnprocessedRequests unprocessedRequests;
    private final ChannelProvider channelProvider;
    private final Bootstrap bootstrap;
    private final EventLoopGroup eventLoopGroup;

    /**
     * @return: null
     * @author: NoFoot
     * @date: 4/19/2023 5:49 PM
     * @description: TODO
     */
    public NettyRpcClient() {
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS));
                        p.addLast(new RpcMessageEncoder());
                        p.addLast(new RpcMessageDecoder());
                        p.addLast(new NettyRpcClientHandler());
                    }
                });
        // TODO 继续

    }

}
