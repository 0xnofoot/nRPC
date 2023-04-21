package xyz.nofoot.proxy;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import xyz.nofoot.config.RpcServiceConfig;
import xyz.nofoot.dto.RpcRequest;
import xyz.nofoot.dto.RpcResponse;
import xyz.nofoot.enums.RpcErrorMessageEnum;
import xyz.nofoot.enums.RpcResponseCodeEnum;
import xyz.nofoot.exception.RpcException;
import xyz.nofoot.netty.NettyRpcClient;
import xyz.nofoot.transport.RpcRequestTransport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.proxy
 * @className: RpcClientProxy
 * @author: NoFoot
 * @date: 4/21/2023 10:22 AM
 * @description: TODO
 */
@Slf4j
public class RpcClientProxy implements InvocationHandler {
    private static final String INTERFACE_NAME = "interfaceName";

    private final RpcRequestTransport rpcRequestTransport;
    private final RpcServiceConfig rpcServiceConfig;

    /**
     * @param rpcRequestTransport:
     * @param rpcServiceConfig:
     * @return: null
     * @author: NoFoot
     * @date: 4/21/2023 10:25 AM
     * @description: TODO
     */
    public RpcClientProxy(RpcRequestTransport rpcRequestTransport, RpcServiceConfig rpcServiceConfig) {
        this.rpcRequestTransport = rpcRequestTransport;
        this.rpcServiceConfig = rpcServiceConfig;
    }

    /**
     * @param rpcRequestTransport:
     * @return: null
     * @author: NoFoot
     * @date: 4/21/2023 10:24 AM
     * @description: TODO
     */
    public RpcClientProxy(RpcRequestTransport rpcRequestTransport) {
        this.rpcRequestTransport = rpcRequestTransport;
        this.rpcServiceConfig = new RpcServiceConfig();
    }

    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    /**
     * @param proxy:
     * @param method:
     * @param args:
     * @return: Object
     * @author: NoFoot
     * @date: 4/21/2023 10:25 AM
     * @description: TODO
     */
    @Override
    @SneakyThrows
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest rpcRequest = RpcRequest.builder()
                .methodName(method.getName())
                .parameters(args)
                .interfaceName(method.getDeclaringClass().getName())
                .parameterTypes(method.getParameterTypes())
                .requestID(UUID.randomUUID().toString())
                .group(rpcServiceConfig.getGroup())
                .version(rpcServiceConfig.getVersion())
                .build();
        RpcResponse<Object> rpcResponse = null;
        if (rpcRequestTransport instanceof NettyRpcClient rpcClient) {
            CompletableFuture<RpcResponse<Object>> completableFuture =
                    (CompletableFuture<RpcResponse<Object>>) rpcClient.sendRpcRequest(rpcRequest);
            rpcResponse = completableFuture.get();
        }
        // 可以有其他的客户端实现
        this.check(rpcResponse, rpcRequest);
        return rpcResponse.getData();
    }

    private void check(RpcResponse<Object> rpcResponse, RpcRequest rpcRequest) {
        if (null == rpcResponse) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }
        if (!rpcRequest.getRequestID().equals(rpcResponse.getRequestId())) {
            throw new RpcException(RpcErrorMessageEnum.REQUEST_NOT_MATCH_RESPONSE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }
        if (null == rpcResponse.getCode() || !rpcResponse.getCode().equals(RpcResponseCodeEnum.SUCCESS.getCode())) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }
    }
}
