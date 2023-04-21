package xyz.nofoot;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import xyz.nofoot.annotation.RpcScan;
import xyz.nofoot.controller.HelloController;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot
 * @className: ClientMain
 * @author: NoFoot
 * @date: 4/21/2023 4:51 PM
 * @description: TODO
 */
@RpcScan(basePackage = {"xyz.nofoot"})
public class ClientMain {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(ClientMain.class);
        HelloController helloController = (HelloController) ctx.getBean("helloController");
        helloController.helloTest();
    }
}
