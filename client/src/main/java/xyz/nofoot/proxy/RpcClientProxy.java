package xyz.nofoot.proxy;

import lombok.extern.slf4j.Slf4j;
import xyz.nofoot.cache.ResultCache;
import xyz.nofoot.config.RpcServiceConfig;
import xyz.nofoot.dto.RpcRequest;
import xyz.nofoot.dto.RpcResponse;
import xyz.nofoot.enums.PropertiesKeyEnum;
import xyz.nofoot.enums.RpcErrorMessageEnum;
import xyz.nofoot.enums.RpcResponseCodeEnum;
import xyz.nofoot.enums.ServiceRegistryEnum;
import xyz.nofoot.exception.RpcException;
import xyz.nofoot.extension.ExtensionLoader;
import xyz.nofoot.netty.NettyRpcClient;
import xyz.nofoot.transport.RpcRequestTransport;
import xyz.nofoot.utils.PropertiesFileUtil;

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
    private final ResultCache resultCache;

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
        String cache = PropertiesFileUtil.getRpcProperty(PropertiesKeyEnum.RPC_REGISTRY.getKey(), ServiceRegistryEnum.ZK.getName());
        this.resultCache = ExtensionLoader.getExtensionLoader(ResultCache.class).getExtension(cache);
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
        String cache = PropertiesFileUtil.getRpcProperty(PropertiesKeyEnum.RPC_REGISTRY.getKey(), ServiceRegistryEnum.ZK.getName());
        this.resultCache = ExtensionLoader.getExtensionLoader(ResultCache.class).getExtension(cache);
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
     * @description: 代理类的执行逻辑，构建 rpcRequest，尝试获取缓存，从远端获取 result
     * 值得注意的是：jdk 代理并没有代理 toString(), hashCode(), equals(), 这三个方法
     * 而且代码中也没有怎么处理，只是单纯判断了 toString() 方法并返回代理的名称信息
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        if (method.getName().equals("toString")) {
            return proxy.getClass().getName() + "@" + proxy.getClass().hashCode();
        }

        RpcRequest rpcRequest = RpcRequest.builder()
                .methodName(method.getName())
                .parameters(args)
                .interfaceName(method.getDeclaringClass().getName())
                .parameterTypes(method.getParameterTypes())
                .requestID(UUID.randomUUID().toString())
                .group(rpcServiceConfig.getGroup())
                .version(rpcServiceConfig.getVersion())
                .build();

        // 获取缓存结果，存在结果直接返回
        Object cacheResult = resultCache.getCacheResult(rpcRequest);
        if (null != cacheResult) {
            return cacheResult;
        }

        RpcResponse<Object> rpcResponse = null;
        if (rpcRequestTransport instanceof NettyRpcClient rpcClient) {
            CompletableFuture<RpcResponse<Object>> completableFuture =
                    (CompletableFuture<RpcResponse<Object>>) rpcClient.sendRpcRequest(rpcRequest);
            // 阻塞发生在此处
            rpcResponse = completableFuture.join();
        }
        // 可以有其他的客户端实现
        this.check(rpcResponse, rpcRequest);
        Object result = rpcResponse.getData();

        // 缓存远程获取的结果
        resultCache.cacheResult(rpcRequest, result);

        return result;
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
