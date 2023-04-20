package xyz.nofoot.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.enums
 * @enumName: SerializationTypeEnum
 * @author: NoFoot
 * @date: 4/20/2023 10:57 AM
 * @description: TODO
 */
@AllArgsConstructor
@Getter
@Slf4j
public enum SerializationTypeEnum {
    KYRO((byte) 0x01, "kyro"),
    PROTOSTUFF((byte) 0x02, "protostuff"),
    HESSIAN((byte) 0x03, "hessian");

    private final byte code;
    private final String name;

    /**
     * @param code:
     * @return: String
     * @author: NoFoot
     * @date: 4/20/2023 11:02 AM
     * @description: TODO
     */
    public static String getName(byte code) {
        for (SerializationTypeEnum c : SerializationTypeEnum.values()) {
            if (c.getCode() == code) {
                return c.name;
            }
        }
        log.error("序列化类型实现不存在, 编号[{}]", code);
        return null;
    }

}
