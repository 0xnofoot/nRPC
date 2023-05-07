package xyz.nofoot.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import xyz.nofoot.dto.RpcRequest;
import xyz.nofoot.enums.PropertiesKeyEnum;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.utils
 * @className: RedisUtil
 * @author: NoFoot
 * @date: 5/5/2023 4:48 PM
 * @description: Redis 工具类
 */
@Slf4j
public final class RedisUtil {
    private static final Set<String> REGISTERED_SERVICE_SET = ConcurrentHashMap.newKeySet();
    private static StringRedisTemplate stringRedisTemplate;
    private static RedisTemplate<String, Object> redisTemplate;
    // 默认 redis 地址，自定义地址请放在 rpc.properties 文件中
    private static final String DEFAULT_REDIS_ADDRESS = "127.0.0.1:6379";
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
     * @return: RedisConnectionFactory
     * @author: NoFoot
     * @date: 5/7/2023 9:05 PM
     * @description: TODO
     */
    private static RedisConnectionFactory getConnectionFactory() {
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
        return lettuceConnectionFactory;
    }

    private static RedisTemplate<String, Object> getRedisTemplate() {
        if (redisTemplate != null) {
            return redisTemplate;
        }
        redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(getConnectionFactory());
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        redisTemplate.setValueSerializer(new GenericToStringSerializer<>(byte[].class));
        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }

    /**
     * @return: StringRedisTemplate
     * @author: NoFoot
     * @date: 5/5/2023 7:32 PM
     * @description: 集成 lettuce 初始化 redis client，获取 stringRedisTemplate
     */
    private static StringRedisTemplate getStringRedisTemplate() {
        if (stringRedisTemplate != null) {
            return stringRedisTemplate;
        }
        stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(getConnectionFactory());
        stringRedisTemplate.afterPropertiesSet();

        return stringRedisTemplate;
    }

    /**
     * @param rpcServiceName:
     * @param inetSocketAddress:
     * @return: void
     * @author: NoFoot
     * @date: 5/6/2023 10:26 AM
     * @description: 添加服务至 Set 集合
     */
    public static void addServiceIdentity(String rpcServiceName, InetSocketAddress inetSocketAddress) {
        String address = inetSocketAddress.toString().substring(1);
        String serviceId = rpcServiceName + "/" + address;
        StringRedisTemplate srt = getStringRedisTemplate();
        try {
            if (Boolean.TRUE.equals(srt.opsForSet().isMember(rpcServiceName, address))) {
                log.info("服务已存在. 服务标识:[{}]", serviceId);
            } else {
                srt.opsForSet().add(rpcServiceName, address);
                log.info("服务成功创建. 服务标识:[{}]", serviceId);
            }
            REGISTERED_SERVICE_SET.add(serviceId);
        } catch (Exception e) {
            log.error("创建服务失败 [{}]", serviceId);
            e.printStackTrace();
        }
    }

    /**
     * @param rpcServiceName:
     * @return: List<String>
     * @author: NoFoot
     * @date: 5/6/2023 11:12 AM
     * @description: 通过服务名获取地址列表
     */
    public static List<String> getServiceUrlList(String rpcServiceName) {
        StringRedisTemplate srt = getStringRedisTemplate();
        Set<String> urlSet = null;
        try {
            urlSet = srt.opsForSet().members(rpcServiceName);

            if (CollectionUtil.isEmpty(urlSet)) {
                log.error("服务地址不存在或为空 [{}]", rpcServiceName);
                System.out.println("tttttttttttttttttttttt");
                return new ArrayList<>();
            }
        } catch (Exception e) {
            log.error("获取地址数据失败 [{}]", rpcServiceName);
            e.printStackTrace();
        }
        assert urlSet != null;
        return new ArrayList<>(urlSet);
    }

    /**
     * @param rpcRequest:
     * @param result:
     * @return: void
     * @author: NoFoot
     * @date: 5/7/2023 8:15 PM
     * @description: TODO
     */
    public static void redisCacheResult(RpcRequest rpcRequest, Object result) {
        String rpcServiceKey = rpcRequest.getRpcServiceName()
                + "_" + Arrays.toString(rpcRequest.getParameterTypes())
                + "_" + Arrays.toString(rpcRequest.getParameters());

        RedisTemplate<String, Object> rt = getRedisTemplate();
        rt.opsForValue().set(rpcServiceKey, result);
    }

    /**
     * @param rpcRequest:
     * @return: Object
     * @author: NoFoot
     * @date: 5/7/2023 8:03 PM
     * @description: TODO
     */
    public static Object getRedisCacheResult(RpcRequest rpcRequest) {
        String rpcServiceKey = rpcRequest.getRpcServiceName()
                + "_" + Arrays.toString(rpcRequest.getParameterTypes())
                + "_" + Arrays.toString(rpcRequest.getParameters());
        RedisTemplate<String, Object> rt = getRedisTemplate();
        return rt.opsForValue().get(rpcServiceKey);
    }


    /**
     * @param inetSocketAddress:
     * @return: void
     * @author: NoFoot
     * @date: 5/6/2023 12:16 PM
     * @description: 根据 address 清除注册的服务
     */
    public static void clearRegistry(InetSocketAddress inetSocketAddress) {
        StringRedisTemplate srt = getStringRedisTemplate();

        REGISTERED_SERVICE_SET.forEach(p -> {
            try {
                if (p.endsWith(inetSocketAddress.toString())) {
                    String rpcServiceName = p.split("/")[0];
                    String address = p.split("/")[1];
                    Long r = srt.opsForSet().remove(rpcServiceName, address);
                    if (null != r && r != 0) {
                        log.info("清除服务成功 [{}]", p);
                    }
                }
            } catch (Exception e) {
                log.error("清除服务失败 [{}]", p);
                e.printStackTrace();
            }
        });
    }
}
