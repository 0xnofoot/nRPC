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
 * @description: TODO
 */
public class UnprocessedRequests {
    private static final Map<String, CompletableFuture<RpcResponse<Object>>>
            UNPROCESSED_RESPONSE_FUTURES = new ConcurrentHashMap<>();

    public void put(String requestId, CompletableFuture<RpcResponse<Object>> future) {
        UNPROCESSED_RESPONSE_FUTURES.put(requestId, future);
    }

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
