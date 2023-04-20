package xyz.nofoot.transport;

import xyz.nofoot.dto.RpcRequest;
import xyz.nofoot.extension.SPI;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.transport
 * @interfaceName: RpcRequestTransport
 * @author: NoFoot
 * @date: 4/20/2023 4:31 PM
 * @description: rpc 请求传输接口
 */
@SPI
public interface RpcRequestTransport {
    /**
     * @param rpcRequest:
     * @return: Object
     * @author: NoFoot
     * @date: 4/20/2023 4:31 PM
     * @description: 发送 rpc请求
     */
    Object sendRpcRequest(RpcRequest rpcRequest);
}
