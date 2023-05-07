package xyz.nofoot.cache.impl;

import xyz.nofoot.cache.ResultCache;
import xyz.nofoot.dto.RpcRequest;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.cache.impl
 * @className: ZkResultCache
 * @author: NoFoot
 * @date: 5/7/2023 8:21 PM
 * @description: TODO
 */
public class ZkResultCache implements ResultCache {
    @Override
    public void cacheResult(RpcRequest rpcRequest, Object result) {
        ResultCache.super.cacheResult(rpcRequest, result);
    }

    @Override
    public Object getCacheResult(RpcRequest rpcRequest) {
        return ResultCache.super.getCacheResult(rpcRequest);
    }
}
