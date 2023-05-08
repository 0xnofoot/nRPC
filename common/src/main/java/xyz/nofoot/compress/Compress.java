package xyz.nofoot.compress;

import xyz.nofoot.extension.SPI;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.compress
 * @interfaceName: Compress
 * @author: NoFoot
 * @date: 4/20/2023 11:27 AM
 * @description: 数据压缩接口
 */
@SPI
public interface Compress {
    /**
     * @param bytes:
     * @return: byte
     * @author: NoFoot
     * @date: 4/20/2023 11:28 AM
     * @description: 数据压缩
     */
    byte[] compress(byte[] bytes);

    /**
     * @param bytes:
     * @return: byte
     * @author: NoFoot
     * @date: 4/20/2023 11:28 AM
     * @description: 数据解压缩
     */
    byte[] deCompress(byte[] bytes);
}
