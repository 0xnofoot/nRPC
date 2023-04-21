package xyz.nofoot.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import xyz.nofoot.config.RpcServerConfig;
import xyz.nofoot.config.RpcServiceConfig;
import xyz.nofoot.netty.handler.NettyRpcServerHandler;
import xyz.nofoot.netty.handler.RpcMessageDecoder;
import xyz.nofoot.netty.handler.RpcMessageEncoder;
import xyz.nofoot.registry.ServiceProvider;
import xyz.nofoot.utils.RuntimeUtil;
import xyz.nofoot.utils.SingletonFactoryUtil;
import xyz.nofoot.utils.threadPool.ThreadPoolFactoryUtil;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.netty
 * @className: NettyRpcServer
 * @author: NoFoot
 * @date: 4/21/2023 10:56 AM
 * @description: TODO
 */
@Slf4j
@Component
public class NettyRpcServer {
    private final ServiceProvider serviceProvider = SingletonFactoryUtil.getInstance(ServiceProvider.class);

    public void registerService(RpcServiceConfig rpcServiceConfig) {
        serviceProvider.publishService(rpcServiceConfig);
    }

    /**
     * @return: void
     * @author: NoFoot
     * @date: 4/21/2023 1:11 PM
     * @description: TODO
     */
    @SneakyThrows
    public void start() {
        NettyShutdownHook.getShutdownHook().clearAll();
        String host = InetAddress.getLocalHost().getHostAddress();
        NioEventLoopGroup boosGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        DefaultEventExecutorGroup serviceHandlerGroup = new DefaultEventExecutorGroup(
                RuntimeUtil.cpus() * 2,
                ThreadPoolFactoryUtil.createThreadFactory("service-handler-group", false)
        );
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(boosGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new IdleStateHandler(30, 0, 0, TimeUnit.HOURS));
                            p.addLast(new RpcMessageEncoder());
                            p.addLast(new RpcMessageDecoder());
                            p.addLast(serviceHandlerGroup, new NettyRpcServerHandler());
                        }
                    });
            ChannelFuture f = b.bind(host, RpcServerConfig.getServerPort()).sync();
            log.info("Server 启动成功 [{}:{}]", host, RpcServerConfig.getServerPort());
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("Server 启动失败: ", e);
        } finally {
            log.warn("关闭所有 eventLoopGroup");
            boosGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            serviceHandlerGroup.shutdownGracefully();
        }
    }
}
