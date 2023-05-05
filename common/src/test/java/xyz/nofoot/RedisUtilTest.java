package xyz.nofoot;

import org.junit.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import xyz.nofoot.utils.RedisUtil;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot
 * @className: RedisUtilTest
 * @author: NoFoot
 * @date: 5/5/2023 5:15 PM
 * @description: TODO
 */
public class RedisUtilTest {
    @Test
    public void getRedisClientTest() {
        StringRedisTemplate s1 = RedisUtil.getStringRedisTemplate();
        s1.opsForValue().set("1", "1");
        StringRedisTemplate s2 = RedisUtil.getStringRedisTemplate();
        s2.opsForValue().set("2", "2");
        s2.opsForValue().set("3", "3");
    }
}
