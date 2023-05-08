package xyz.nofoot.cache.impl;

import xyz.nofoot.cache.ResultCache;
import xyz.nofoot.dto.RpcRequest;
import xyz.nofoot.utils.RedisUtil;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.cache.impl
 * @className: RedisResultCache
 * @author: NoFoot
 * @date: 5/7/2023 8:20 PM
 * @description: TODO
 */
public class RedisResultCache implements ResultCache {
    /**
     * @param rpcRequest:
     * @param result:
     * @return: void
     * @author: NoFoot
     * @date: 5/8/2023 5:38 PM
     * @description: TODO
     */
    @Override
    public void cacheResult(RpcRequest rpcRequest, Object result) {
        RedisUtil.redisCacheResult(rpcRequest, result);
    }

    /**
     * @param rpcRequest:
     * @return: Object
     * @author: NoFoot
     * @date: 5/8/2023 5:38 PM
     * @description: TODO
     */
    @Override
    public Object getCacheResult(RpcRequest rpcRequest) {
        return RedisUtil.getRedisCacheResult(rpcRequest);
    }
}
