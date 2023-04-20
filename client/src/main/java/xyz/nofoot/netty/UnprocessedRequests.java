package xyz.nofoot.netty;

import xyz.nofoot.dto.RpcResponse;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.netty
 * @className: UnprocessedRequests
 * @author: NoFoot
 * @date: 4/19/2023 4:58 PM
 * @description: 未处理任务（请求）类
 */
public class UnprocessedRequests {
    private static final Map<String, CompletableFuture<RpcResponse<Object>>>
            UNPROCESSED_RESPONSE_FUTURES = new ConcurrentHashMap<>();

    /**
     * @param requestId:
     * @param future:
     * @return: void
     * @author: NoFoot
     * @date: 4/20/2023 4:39 PM
     * @description: 添加一个异步处理请求
     */
    public void put(String requestId, CompletableFuture<RpcResponse<Object>> future) {
        UNPROCESSED_RESPONSE_FUTURES.put(requestId, future);
    }

    /**
     * @param rpcResponse:
     * @return: void
     * @author: NoFoot
     * @date: 4/20/2023 4:39 PM
     * @description: 根据Server端返回的response的请求id，从map中拿到future任务，执行complete传入结果
     */
    public void complete(RpcResponse<Object> rpcResponse) {
        CompletableFuture<RpcResponse<Object>> future =
                UNPROCESSED_RESPONSE_FUTURES.remove(rpcResponse.getRequestId());

        if (null != future) {
            future.complete(rpcResponse);
        } else {
            throw new IllegalArgumentException();
        }
    }
}
