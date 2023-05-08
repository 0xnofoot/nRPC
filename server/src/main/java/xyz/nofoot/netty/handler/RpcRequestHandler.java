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
 * @description: rpcRequest 的处理类
 */
@Slf4j
public class RpcRequestHandler {
    private final ServiceProvider serviceProvider;

    /**
     * @return: null
     * @author: NoFoot
     * @date: 4/21/2023 1:30 PM
     * @description: 构造，赋值服务发现类
     */
    public RpcRequestHandler() {
        serviceProvider = SingletonFactoryUtil.getInstance(ServiceProvider.class);
    }

    /**
     * @param rpcRequest:
     * @return: Object
     * @author: NoFoot
     * @date: 4/21/2023 1:30 PM
     * @description: rpcRequest 的处理函数
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
     * @description: request 的方法的具体执行，并返回执行结果
     */
    private Object invokeTargetMethod(RpcRequest rpcRequest, Object service) {
        Object result;
        try {
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
            result = method.invoke(service, rpcRequest.getParameters());
            log.debug("服务[{}] 成功执行方法[{}]", rpcRequest.getRpcServiceName(), rpcRequest.getMethodName());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RpcException(e.getMessage(), e);
        }
        return result;
    }


}
