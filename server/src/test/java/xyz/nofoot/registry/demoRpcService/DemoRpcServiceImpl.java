package xyz.nofoot.registry.demoRpcService;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.registry
 * @className: DemoRpcServiceImpl
 * @author: NoFoot
 * @date: 4/17/2023 4:05 PM
 * @description TODO
 */
public class DemoRpcServiceImpl implements DemoRpcService {
    @Override
    public String hello() {
        return "hello demo rpc service";
    }
}
