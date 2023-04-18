package xyz.nofoot.loadbalance;

import xyz.nofoot.dto.RpcRequest;
import xyz.nofoot.extension.SPI;

import java.util.List;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.loadbalance
 * @interfaceName: LoadBalance
 * @author: NoFoot
 * @date: 4/17/23 11:47 PM
 * @description: 负载均衡接口
 */
@SPI
public interface LoadBalance {

    /**
     * @param serviceUrlList:
     * @param rpcRequest:
     * @return: String
     * @author: NoFoot
     * @date: 4/17/23 11:49 PM
     * @description: 根据服务列表，选择合适的服务信息
     */
    String selectServerAddress(List<String> serviceUrlList, RpcRequest rpcRequest);
}
