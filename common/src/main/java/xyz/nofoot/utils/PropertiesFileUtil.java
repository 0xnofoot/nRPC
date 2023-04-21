package xyz.nofoot.utils;

import lombok.extern.slf4j.Slf4j;

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
}
