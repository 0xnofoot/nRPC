package xyz.nofoot.serialize;

import xyz.nofoot.extension.SPI;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.serialize
 * @interfaceName: Serializer
 * @author: NoFoot
 * @date: 4/20/2023 11:23 AM
 * @description: 序列化接口
 */
@SPI
public interface Serializer {
    /**
     * @param obj: 要序列化的对象
     * @return: byte
     * @author: NoFoot
     * @date: 4/20/2023 11:23 AM
     * @description: 执行序列化
     */
    byte[] serialize(Object obj);

    /**
     * @param bytes:
     * @param clazz:
     * @return: T
     * @author: NoFoot
     * @date: 4/20/2023 11:24 AM
     * @description: 反序列化
     */
    <T> T deserialize(byte[] bytes, Class<T> clazz);
}
