package xyz.nofoot.loadbalance;

import xyz.nofoot.dto.RpcRequest;
import xyz.nofoot.utils.CollectionUtil;

import java.util.List;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.loadbalance
 * @className: AbstractLoadBalance
 * @author: NoFoot
 * @date: 4/17/23 11:53 PM
 * @description: 对服务地址进行负载均衡的抽象类
 */
public abstract class AbstractLoadBalance implements LoadBalance {

    /**
     * @param serviceAddresses: 服务地址列表
     * @param rpcRequest:       rpc 请求消息
     * @return: String
     * @author: NoFoot
     * @date: 4/18/23 12:02 AM
     * @description: 校验服务列表，调用 doSelect() 函数
     */
    @Override
    public String selectServerUrl(List<String> serviceAddresses, RpcRequest rpcRequest) {
        if (CollectionUtil.isEmpty(serviceAddresses)) {
            return null;
        }
        if (serviceAddresses.size() == 1) {
            return serviceAddresses.get(0);
        }

        return doSelect(serviceAddresses, rpcRequest);
    }

    /**
     * @param serviceAddresses:
     * @param rpcRequest:
     * @return: String
     * @author: NoFoot
     * @date: 4/18/23 12:02 AM
     * @description: 负载均衡逻辑的抽象方法，继承该类后重写该方法实现负载均衡
     */
    protected abstract String doSelect(List<String> serviceAddresses, RpcRequest rpcRequest);
}
