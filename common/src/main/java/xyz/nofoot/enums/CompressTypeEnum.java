package xyz.nofoot.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.enums
 * @enumName: CompressTypeEnum
 * @author: NoFoot
 * @date: 4/20/2023 11:02 AM
 * @description: TODO
 */
@AllArgsConstructor
@Getter
@Slf4j
public enum CompressTypeEnum {
    GZIP((byte) 0x01, "gzip");

    private final byte code;
    private final String name;


    /**
     * @param code:
     * @return: String
     * @author: NoFoot
     * @date: 4/20/2023 11:05 AM
     * @description: TODO
     */
    public static String getName(byte code) {
        for (CompressTypeEnum c : CompressTypeEnum.values()) {
            if (c.getCode() == code) {
                return c.name;
            }
        }
        log.error("压缩类型实现不存在, 编号[{}]", code);
        return null;
    }
}
