package xyz.nofoot.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import xyz.nofoot.compress.Compress;
import xyz.nofoot.dto.RpcRequest;
import xyz.nofoot.enums.CompressTypeEnum;
import xyz.nofoot.enums.PropertiesKeyEnum;
import xyz.nofoot.enums.SerializationTypeEnum;
import xyz.nofoot.extension.ExtensionLoader;
import xyz.nofoot.serialize.Serializer;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

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
    private static RedisTemplate<Long, byte[]> resultRedisTemplate;
    // 默认 redis 地址，自定义地址请放在 rpc.properties 文件中
    private static final String DEFAULT_REDIS_ADDRESS = "127.0.0.1:6379";
    private static final String DEFAULT_REDIS_USERNAME = null;
    private static final String DEFAULT_REDIS_PASSWORD = null;
    private static final long CACHE_TTL = 30;
    private static final TimeUnit CACHE_TTL_TIME_UNIT = TimeUnit.SECONDS;

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
     * @description: 集成 lettuce 初始化 redis configuration
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

    /**
     * @return: RedisTemplate<Long, Object>
     * @author: NoFoot
     * @date: 5/8/2023 12:37 PM
     * @description: 获取用于 result cache 的 redisTemplate
     */
    private static RedisTemplate<Long, byte[]> getResultRedisTemplate() {
        if (resultRedisTemplate != null) {
            return resultRedisTemplate;
        }
        resultRedisTemplate = new RedisTemplate<>();
        resultRedisTemplate.setConnectionFactory(getConnectionFactory());
        resultRedisTemplate.setKeySerializer(new GenericToStringSerializer<>(Long.class));
        resultRedisTemplate.afterPropertiesSet();

        return resultRedisTemplate;
    }

    /**
     * @return: StringRedisTemplate
     * @author: NoFoot
     * @date: 5/5/2023 7:32 PM
     * @description: 获取 stringRedisTemplate
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
     * @param key:
     * @return: long
     * @author: NoFoot
     * @date: 5/8/2023 12:49 PM
     * @description: TODO
     */
    private static long getKeyHash(String key) {
        MessageDigest md;
        byte[] mdDigest;

        try {
            md = MessageDigest.getInstance("MD5");
            byte[] bytes = key.getBytes(StandardCharsets.UTF_8);
            md.update(bytes);
            mdDigest = md.digest();
        } catch (NoSuchAlgorithmException e) {
            log.error("error in md5");
            e.printStackTrace();
            return key.hashCode();
        }

        return ((long) (mdDigest[3 + 3 * 4] & 255) << 24
                | (long) (mdDigest[2 + 3 * 4] & 255) << 16
                | (long) (mdDigest[1 + 3 * 4] & 255) << 8
                | (long) (mdDigest[3 * 4] & 255)) & 4294967295L;
    }

    /**
     * @param result:
     * @return: byte
     * @author: NoFoot
     * @date: 5/8/2023 12:55 PM
     * @description: Redis 存取对象的默认 序列化和压缩
     */
    private static byte[] resultToBytes(Object result) {
        Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class)
                .getExtension(SerializationTypeEnum.PROTOSTUFF.getName());
        byte[] serializeData = serializer.serialize(result);
        Compress compress = ExtensionLoader.getExtensionLoader(Compress.class)
                .getExtension(CompressTypeEnum.GZIP.getName());
        return compress.compress(serializeData);
    }

    /**
     * @param bytes:
     * @return: Object
     * @author: NoFoot
     * @date: 5/8/2023 12:55 PM
     * @description: Redis 存取对象的默认 反序列化和反压缩
     */
    private static Object bytesToResult(byte[] bytes) {
        Compress compress = ExtensionLoader.getExtensionLoader(Compress.class)
                .getExtension(CompressTypeEnum.GZIP.getName());
        byte[] compressResult = compress.deCompress(bytes);
        Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class)
                .getExtension(SerializationTypeEnum.PROTOSTUFF.getName());
        return serializer.deserialize(compressResult, Object.class);
    }

    /**
     * @param rpcRequest:
     * @param result:
     * @return: void
     * @author: NoFoot
     * @date: 5/7/2023 8:15 PM
     * @description: 缓存远程返回的结果，有效期 30s
     */
    public static void redisCacheResult(RpcRequest rpcRequest, Object result) {
        String rpcServiceKey = rpcRequest.getRpcServiceName()
                + "_" + Arrays.toString(rpcRequest.getParameterTypes())
                + "_" + Arrays.toString(rpcRequest.getParameters());
        long hashKey = getKeyHash(rpcServiceKey);

        byte[] resultBytes = resultToBytes(result);

        RedisTemplate<Long, byte[]> rrt = getResultRedisTemplate();
        rrt.opsForValue().set(hashKey, resultBytes, CACHE_TTL, CACHE_TTL_TIME_UNIT);
    }

    /**
     * @param rpcRequest:
     * @return: Object
     * @author: NoFoot
     * @date: 5/7/2023 8:03 PM
     * @description: 从 Redis 获取缓存的结果，没有则为 null
     */
    public static Object getRedisCacheResult(RpcRequest rpcRequest) {
        String rpcServiceKey = rpcRequest.getRpcServiceName()
                + "_" + Arrays.toString(rpcRequest.getParameterTypes())
                + "_" + Arrays.toString(rpcRequest.getParameters());

        RedisTemplate<Long, byte[]> rrt = getResultRedisTemplate();
        byte[] bytesResult = rrt.opsForValue().get(rpcServiceKey);
        Object result = null;

        if (null != bytesResult) {
            long hashKey = getKeyHash(rpcServiceKey);
            rrt.expire(hashKey, CACHE_TTL, CACHE_TTL_TIME_UNIT);
            redisCacheResult(rpcRequest, result);
            result = bytesToResult(bytesResult);
        }

        return result;
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
