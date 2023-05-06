package xyz.nofoot.serviceImpl;

import xyz.nofoot.dto.Hello;
import lombok.extern.slf4j.Slf4j;
import xyz.nofoot.service.HelloService;
import xyz.nofoot.annotation.RpcService;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.serviceImpl
 * @className: HelloServiceImpl_1
 * @author: NoFoot
 * @date: 4/21/2023 2:18 PM
 * @description: TODO
 */
@RpcService(group = "test1", version = "v1")
@Slf4j
public class HelloServiceImpl_1 implements HelloService {
    @Override
    public String hello(Hello hello) {
        log.info("[{}] 被调用", this.getClass().getCanonicalName());
        return "I am HelloServiceImpl_1 ";
    }
}
