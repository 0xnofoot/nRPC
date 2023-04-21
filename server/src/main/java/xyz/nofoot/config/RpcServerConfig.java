package xyz.nofoot.config;


import lombok.extern.slf4j.Slf4j;
import xyz.nofoot.enums.PropertiesKeyEnum;
import xyz.nofoot.utils.PropertiesFileUtil;

import java.util.Properties;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.config
 * @className: RpcServerConfig
 * @author: NoFoot
 * @date: 4/19/23 12:04 AM
 * @description: TODO
 */
@Slf4j
public class RpcServerConfig {
    // 默认 Server 启动端口
    public static final int DEFAULT_SERVER_PORT = 9290;
    public static int SERVER_PORT = -1;

    /**
     * @return: int
     * @author: NoFoot
     * @date: 4/21/2023 3:31 PM
     * @description: TODO
     */
    public static int getServerPort() {
        if (SERVER_PORT != -1) {
            return SERVER_PORT;
        }
        int port = DEFAULT_SERVER_PORT;
        // 如果配置文件中配置了端口号，则读取配置文件内容
        Properties properties = PropertiesFileUtil.readPropertiesFile(PropertiesKeyEnum.RPC_CONFIG_PATH.getKey());
        if (null != properties && null != properties.getProperty(PropertiesKeyEnum.PORT.getKey())) {
            String p = properties.getProperty(PropertiesKeyEnum.PORT.getKey());
            port = Integer.parseInt(p);
            SERVER_PORT = port;
            log.info("读取到端口配置，使用自定义 Server 启动端口：[{}]", port);
        }
        return port;
    }
}
