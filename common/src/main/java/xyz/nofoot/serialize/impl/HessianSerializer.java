package xyz.nofoot.serialize.impl;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import xyz.nofoot.serialize.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.serialize.impl
 * @className: HessianSerializer
 * @author: NoFoot
 * @date: 4/21/2023 4:36 PM
 * @description: TODO
 */
public class HessianSerializer implements Serializer {

    /**
     * @param obj:
     * @return: byte
     * @author: NoFoot
     * @date: 4/21/2023 4:37 PM
     * @description: TODO
     */
    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            HessianOutput hessianOutput = new HessianOutput(out);
            hessianOutput.writeObject(obj);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Hessian 序列化错误", e);
        }
    }

    /**
     * @param bytes:
     * @param clazz:
     * @return: T
     * @author: NoFoot
     * @date: 4/21/2023 4:37 PM
     * @description: TODO
     */
    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try (ByteArrayInputStream in = new ByteArrayInputStream(bytes)) {
            HessianInput hessianInput = new HessianInput(in);
            Object o = hessianInput.readObject();
            return clazz.cast(o);
        } catch (IOException e) {
            throw new RuntimeException("Hessian 反序列化错误", e);
        }
    }
}
