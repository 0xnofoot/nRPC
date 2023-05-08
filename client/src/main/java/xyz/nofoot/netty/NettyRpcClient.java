package xyz.nofoot.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import xyz.nofoot.constants.RpcConstants;
import xyz.nofoot.dto.RpcMessage;
import xyz.nofoot.dto.RpcRequest;
import xyz.nofoot.dto.RpcResponse;
import xyz.nofoot.enums.CompressTypeEnum;
import xyz.nofoot.enums.PropertiesKeyEnum;
import xyz.nofoot.enums.SerializationTypeEnum;
import xyz.nofoot.enums.ServiceRegistryEnum;
import xyz.nofoot.extension.ExtensionLoader;
import xyz.nofoot.netty.handler.NettyRpcClientHandler;
import xyz.nofoot.netty.handler.RpcMessageDecoder;
import xyz.nofoot.netty.handler.RpcMessageEncoder;
import xyz.nofoot.registry.ServiceDiscovery;
import xyz.nofoot.transport.RpcRequestTransport;
import xyz.nofoot.utils.PropertiesFileUtil;
import xyz.nofoot.utils.SingletonFactoryUtil;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.netty
 * @className: NettyRpcClient
 * @author: NoFoot
 * @date: 4/19/2023 4:56 PM
 * @description: netty 实现的客户端 Client
 */
@Slf4j
public class NettyRpcClient implements RpcRequestTransport {
    private final ServiceDiscovery serviceDiscovery;
    private final UnprocessedRequests unprocessedRequests;
    private final ChannelProvider channelProvider;
    private final Bootstrap bootstrap;
    private final EventLoopGroup eventLoopGroup;

    /**
     * @return: null
     * @author: NoFoot
     * @date: 4/19/2023 5:49 PM
     * @description: 构造 Client
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
                    protected void initChannel(Channel ch) {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new IdleStateHandler(0, 1, 0, TimeUnit.MINUTES));
                        p.addLast(new RpcMessageEncoder());
                        p.addLast(new RpcMessageDecoder());
                        p.addLast(new NettyRpcClientHandler());
                    }
                });

        String registry = PropertiesFileUtil.getRpcProperty(PropertiesKeyEnum.RPC_REGISTRY.getKey(), ServiceRegistryEnum.ZK.getName());
        this.serviceDiscovery = ExtensionLoader.getExtensionLoader(ServiceDiscovery.class)
                .getExtension(registry);
        this.unprocessedRequests = SingletonFactoryUtil.getInstance(UnprocessedRequests.class);
        this.channelProvider = SingletonFactoryUtil.getInstance(ChannelProvider.class);
    }

    /**
     * @param rpcRequest:
     * @return: Object
     * @author: NoFoot
     * @date: 4/20/2023 4:23 PM
     * @description: 发送 request
     */
    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {
        CompletableFuture<RpcResponse<Object>> resultFuture = new CompletableFuture<>();
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest);
        Channel channel = getChannel(inetSocketAddress);
        if (channel.isActive()) {
            unprocessedRequests.put(rpcRequest.getRequestID(), resultFuture);
            RpcMessage rpcMessage = RpcMessage.builder()
                    .data(rpcRequest)
                    .codec(SerializationTypeEnum.PROTOSTUFF.getCode())
                    .compress(CompressTypeEnum.GZIP.getCode())
                    .messageType(RpcConstants.REQUEST_TYPE).build();
            channel.writeAndFlush(rpcMessage).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.debug("Client 发送消息：[{}]", rpcMessage);
                } else {
                    future.channel().close();
                    resultFuture.completeExceptionally(future.cause());
                    log.error("Client 消息发送失败: ", future.cause());
                }
            });
        } else {
            throw new IllegalArgumentException();
        }
        return resultFuture;
    }

    /**
     * @param inetSocketAddress:
     * @return: Channel
     * @author: NoFoot
     * @date: 4/20/2023 10:47 AM
     * @description: 执行连接操作
     */
    @SneakyThrows
    private Channel doConnect(InetSocketAddress inetSocketAddress) {
        CompletableFuture<Channel> channelCompletableFuture = new CompletableFuture<>();
        bootstrap.connect(inetSocketAddress).addListener(
                (ChannelFutureListener) future -> {
                    if (future.isSuccess()) {
                        log.debug("Client 连接 Server [{}] 成功", inetSocketAddress);
                        channelCompletableFuture.complete(future.channel());
                    } else {
                        throw new IllegalArgumentException();
                    }
                }
        );
        return channelCompletableFuture.get();
    }

    /**
     * @param inetSocketAddress:
     * @return: Channel
     * @author: NoFoot
     * @date: 4/20/2023 10:47 AM
     * @description: 根据 Server 地址获取 Channel
     */
    public Channel getChannel(InetSocketAddress inetSocketAddress) {
        Channel channel = channelProvider.get(inetSocketAddress);
        if (null == channel) {
            channel = doConnect(inetSocketAddress);
            channelProvider.set(inetSocketAddress, channel);
        }
        return channel;
    }

    /**
     * @return: void
     * @author: NoFoot
     * @date: 4/20/2023 9:08 PM
     * @description: TODO
     */
    public void close() {
        eventLoopGroup.shutdownGracefully();
    }

}
