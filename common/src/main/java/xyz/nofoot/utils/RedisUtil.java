package xyz.nofoot.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import xyz.nofoot.enums.PropertiesKeyEnum;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.utils
 * @className: RedisUtil
 * @author: NoFoot
 * @date: 5/5/2023 4:48 PM
 * @description: TODO
 */
@Slf4j
public final class RedisUtil {
    private static final Map<String, List<String>> SERVICE_ADDRESS_MAP = new ConcurrentHashMap<>();
    private static final Set<String> REGISTERED_PATH_SET = ConcurrentHashMap.newKeySet();
    private static StringRedisTemplate stringRedisTemplate;
    // 默认 redis 地址，自定义地址请放在 rpc.properties 文件中
//    private static final String DEFAULT_REDIS_ADDRESS = "127.0.0.1:6379";
    private static final String DEFAULT_REDIS_ADDRESS = "192.168.1.101:6379";
    private static final String DEFAULT_REDIS_USERNAME = null;
    private static final String DEFAULT_REDIS_PASSWORD = null;

    /**
     * @author: NoFoot
     * @date: 5/5/2023 7:47 PM
     * @description: 私有无参构造
     */
    private RedisUtil() {
    }

    /**
     * @return: StringRedisTemplate
     * @author: NoFoot
     * @date: 5/5/2023 7:32 PM
     * @description: TODO
     */
    public static StringRedisTemplate getStringRedisTemplate() {
        if (stringRedisTemplate != null) {
            return stringRedisTemplate;
        }

        String redisAddress = PropertiesFileUtil.getRpcProperty(PropertiesKeyEnum.REDIS_ADDRESS.getKey(), DEFAULT_REDIS_ADDRESS);
        String redisUsername = PropertiesFileUtil.getRpcProperty(PropertiesKeyEnum.REDIS_USERNAME.getKey(), DEFAULT_REDIS_USERNAME);
        String redisPassword = PropertiesFileUtil.getRpcProperty(PropertiesKeyEnum.REDIS_PASSWORD.getKey(), DEFAULT_REDIS_PASSWORD);
        String hostName = redisAddress.split(":")[0];
        int port = Integer.parseInt(redisAddress.split(":")[1]);

        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(hostName);
        redisStandaloneConfiguration.setPort(port);
        if (null != redisUsername) {
            redisStandaloneConfiguration.setUsername(redisUsername);
        }
        if (null != redisPassword) {
            redisStandaloneConfiguration.setPassword(redisPassword);
        }

        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisStandaloneConfiguration);
        lettuceConnectionFactory.afterPropertiesSet();
        stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(lettuceConnectionFactory);
        stringRedisTemplate.afterPropertiesSet();

        return stringRedisTemplate;
    }
}
