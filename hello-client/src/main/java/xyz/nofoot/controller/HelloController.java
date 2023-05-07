package xyz.nofoot.controller;

import lombok.extern.slf4j.Slf4j;
import xyz.nofoot.annotation.RpcReference;
import xyz.nofoot.dto.Hello;
import xyz.nofoot.service.HelloService;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.controller
 * @className: HelloController
 * @author: NoFoot
 * @date: 4/21/2023 4:53 PM
 * @description: TODO
 */
@RpcReference
@Slf4j
public class HelloController {
    @RpcReference(group = "test1", version = "v1")
    private HelloService helloService1;

    @RpcReference(group = "test2", version = "v1")
    private HelloService helloService2;

    public void helloTest() {
        log.info("helloService1 执行 hello()方法");
        String r1 = helloService1.hello(new Hello("111", "from helloService 1"));
        log.info("helloService1 执行结果[{}]", r1);


        log.info("helloService2 执行 hello()方法");
        String r2 = helloService2.hello(new Hello("222", "from helloService 2"));
        log.info("helloService2 执行结果[{}]", r2);

    }

}
