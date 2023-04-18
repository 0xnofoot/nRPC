package xyz.nofoot.loadbalance.loadbalancer;

import xyz.nofoot.dto.RpcRequest;
import xyz.nofoot.loadbalance.AbstractLoadBalance;

import java.util.List;
import java.util.Random;


/**
 * @projectName: nRPC
 * @package: xyz.nofoot.loadbalance.loadbalancer
 * @className: RandomLoadBalance
 * @author: NoFoot
 * @date: 4/18/23 12:07 AM
 * @description: 随机负载均衡策略
 */
public class RandomLoadBalance extends AbstractLoadBalance {

    /**
     * @param serviceAddresses:
     * @param rpcRequest:
     * @return: String
     * @author: NoFoot
     * @date: 4/18/23 12:12 AM
     * @description: 随机负载均衡策略实现逻辑
     */
    @Override
    protected String doSelect(List<String> serviceAddresses, RpcRequest rpcRequest) {
        Random random = new Random();
        return serviceAddresses.get(random.nextInt(serviceAddresses.size()));
    }
}
