package xyz.nofoot;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import xyz.nofoot.annotation.RpcScan;
import xyz.nofoot.netty.NettyRpcServer;

/**
 * @projectName: nRPC
 * @package: PACKAGE_NAME
 * @className: xyz.nofoot.ServerMain
 * @author: NoFoot
 * @date: 4/21/2023 2:04 PM
 * @description: TODO
 */
@RpcScan(basePackage = {"xyz.nofoot"})
public class ServerMain {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(ServerMain.class);
        NettyRpcServer nettyRpcServer = (NettyRpcServer) ctx.getBean("nettyRpcServer");
        nettyRpcServer.start();
    }
}
