package xyz.nofoot.utils;

import lombok.extern.slf4j.Slf4j;
import xyz.nofoot.enums.PropertiesKeyEnum;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.utils
 * @className: PropertiesFileUtil
 * @author: NoFoot
 * @date: 4/17/2023 2:37 PM
 * @description: properties 文件工具类
 */
@Slf4j
public final class PropertiesFileUtil {

    /**
     * @author: NoFoot
     * @date: 4/17/2023 2:39 PM
     * @description: 私有构造
     */
    private PropertiesFileUtil() {
    }

    /**
     * @param fileName: properties文件名
     * @return: Properties
     * @author: NoFoot
     * @date: 4/17/2023 3:02 PM
     * @description: 解析 properties文件，返回 properties 对象
     */
    public static Properties readPropertiesFile(String fileName) {
        URL url = Thread.currentThread().getContextClassLoader().getResource("");
        String rpcConfigPath = "";

        if (url != null) {
            rpcConfigPath = URLDecoder.decode(url.getPath(), StandardCharsets.UTF_8) + fileName;
        }
        Properties properties = null;
        try (InputStreamReader inputStreamReader = new InputStreamReader(
                new FileInputStream(rpcConfigPath), StandardCharsets.UTF_8
        )) {
            properties = new Properties();
            properties.load(inputStreamReader);
        } catch (IOException e) {
            log.warn("配置文件中未配置 [{}], 可能会采用默认值", fileName);
        }
        return properties;
    }


    /**
     * @param fileName:
     * @param key:
     * @param defaultValue:
     * @return: String
     * @author: NoFoot
     * @date: 5/5/2023 6:22 PM
     * @description: TODO
     */
    public static String getProperty(String fileName, String key, String defaultValue) {
        Properties properties = readPropertiesFile(fileName);
        if (properties == null) {
            log.error("配置文件错误 [{}]", fileName);
            return defaultValue;
        }
        return properties.getProperty(key, defaultValue);
    }

    /**
     * @param fileName:
     * @param key:
     * @return: String
     * @author: NoFoot
     * @date: 5/5/2023 6:23 PM
     * @description: TODO
     */
    public static String getProperty(String fileName, String key) {
        return getProperty(fileName, key, null);
    }

    /**
     * @param key:
     * @param defaultValue:
     * @return: String
     * @author: NoFoot
     * @date: 5/5/2023 6:24 PM
     * @description: TODO
     */
    public static String getRpcProperty(String key, String defaultValue) {
        return getProperty(PropertiesKeyEnum.RPC_CONFIG_PATH.getKey(), key, defaultValue);
    }

    /**
     * @param key:
     * @return: String
     * @author: NoFoot
     * @date: 5/5/2023 6:24 PM
     * @description: TODO
     */
    public static String getRpcProperty(String key) {
        return getProperty(PropertiesKeyEnum.RPC_CONFIG_PATH.getKey(), key);
    }
}
