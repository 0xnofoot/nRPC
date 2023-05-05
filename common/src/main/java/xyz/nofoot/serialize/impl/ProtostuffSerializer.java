package xyz.nofoot.serialize.impl;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import xyz.nofoot.serialize.Serializer;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.serialize.impl
 * @className: ProtostuffSerializer
 * @author: NoFoot
 * @date: 5/5/2023 11:42 AM
 * @description: TODO
 */
public class ProtostuffSerializer implements Serializer {
    private static final LinkedBuffer BUFFER = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);

    /**
     * @param obj:
     * @return: byte
     * @author: NoFoot
     * @date: 5/5/2023 11:57 AM
     * @description: TODO
     */
    @Override
    public byte[] serialize(Object obj) {
        Class<?> clazz = obj.getClass();
        Schema schema = RuntimeSchema.getSchema(clazz);
        byte[] bytes;
        try {
            bytes = ProtostuffIOUtil.toByteArray(obj, schema, BUFFER);
        } finally {
            BUFFER.clear();
        }
        return bytes;
    }

    /**
     * @param bytes:
     * @param clazz:
     * @return: T
     * @author: NoFoot
     * @date: 5/5/2023 11:57 AM
     * @description: TODO
     */
    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        Schema<T> schema = RuntimeSchema.getSchema(clazz);
        T obj = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(bytes, obj, schema);
        return obj;
    }
}
