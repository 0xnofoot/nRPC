package xyz.nofoot.compress.impl;

import xyz.nofoot.compress.Compress;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.compress.impl
 * @className: GzipCompress
 * @author: NoFoot
 * @date: 4/21/2023 4:19 PM
 * @description: TODO
 */
public class GzipCompress implements Compress {

    private static final int BUFFER_SIZE = 1024 * 4;

    /**
     * @param bytes:
     * @return: byte
     * @author: NoFoot
     * @date: 4/21/2023 4:20 PM
     * @description: TODO
     */
    @Override
    public byte[] compress(byte[] bytes) {
        if (bytes == null) {
            throw new NullPointerException("bytes 为空，压缩失败");
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            GZIPOutputStream gzip = new GZIPOutputStream(out);
            gzip.write(bytes);
            gzip.flush();
            gzip.finish();
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("gzip 压缩错误", e);
        }
    }

    /**
     * @param bytes:
     * @return: byte
     * @author: NoFoot
     * @date: 4/21/2023 4:20 PM
     * @description: TODO
     */
    @Override
    public byte[] deCompress(byte[] bytes) {
        if (null == bytes) {
            throw new NullPointerException("byte 为空，解压缩失败");
        }
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(bytes))) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int n;
            while ((n = gzip.read(buffer)) > -1) {
                out.write(buffer, 0, n);
            }
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("gzip 解压缩错误", e);
        }
    }
}
