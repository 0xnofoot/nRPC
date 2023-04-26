package xyz.nofoot.serialize.impl;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import xyz.nofoot.dto.RpcRequest;
import xyz.nofoot.dto.RpcResponse;
import xyz.nofoot.serialize.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.serialize.impl
 * @className: KyroSerializer
 * @author: NoFoot
 * @date: 4/26/2023 3:35 PM
 * @description: TODO
 */
public class KyroSerializer implements Serializer {

    private final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kyro = new Kryo();
        kyro.register(RpcResponse.class);
        kyro.register(RpcRequest.class);
        return kyro;
    });

    /**
     * @param obj:
     * @return: byte
     * @author: NoFoot
     * @date: 4/26/2023 3:39 PM
     * @description: TODO
     */
    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             Output output = new Output(baos)) {
            Kryo kryo = kryoThreadLocal.get();
            kryo.writeObject(output, obj);
            kryoThreadLocal.remove();
            return output.toBytes();
        } catch (IOException e) {
            throw new RuntimeException("Kryo 反序列化错误", e);
        }
    }

    /**
     * @param bytes:
     * @param clazz:
     * @return: T
     * @author: NoFoot
     * @date: 4/26/2023 3:39 PM
     * @description: TODO
     */
    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
            Input input = new Input(bais);
            Kryo kryo = kryoThreadLocal.get();
            Object o = kryo.readObject(input, clazz);
            kryoThreadLocal.remove();
            return clazz.cast(o);
        } catch (IOException e) {
            throw new RuntimeException("Kryo 反序列化错误", e);
        }
    }

}
