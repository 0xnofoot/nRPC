package xyz.nofoot.netty.handler;

import lombok.extern.slf4j.Slf4j;
import xyz.nofoot.dto.RpcRequest;
import xyz.nofoot.exception.RpcException;
import xyz.nofoot.registry.ServiceProvider;
import xyz.nofoot.utils.SingletonFactoryUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.netty.handler
 * @className: RpcRequestHandler
 * @author: NoFoot
 * @date: 4/21/2023 1:28 PM
 * @description: TODO
 */
@Slf4j
public class RpcRequestHandler {
    private final ServiceProvider serviceProvider;

    /**
     * @param :
     * @return: null
     * @author: NoFoot
     * @date: 4/21/2023 1:30 PM
     * @description: TODO
     */
    public RpcRequestHandler() {
        serviceProvider = SingletonFactoryUtil.getInstance(ServiceProvider.class);
    }

    /**
     * @param rpcRequest:
     * @return: Object
     * @author: NoFoot
     * @date: 4/21/2023 1:30 PM
     * @description: TODO
     */
    public Object handle(RpcRequest rpcRequest) {
        Object service = serviceProvider.getService(rpcRequest.getRpcServiceName());
        return invokeTargetMethod(rpcRequest, service);
    }

    /**
     * @param rpcRequest:
     * @param service:
     * @return: Object
     * @author: NoFoot
     * @date: 4/21/2023 1:30 PM
     * @description: TODO
     */
    private Object invokeTargetMethod(RpcRequest rpcRequest, Object service) {
        Object result;
        try {
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
            result = method.invoke(service, rpcRequest.getParameters());
            log.info("服务[{}] 成功执行方法[{}]", rpcRequest.getRpcServiceName(), rpcRequest.getMethodName());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RpcException(e.getMessage(), e);
        }
        return result;
    }


}
