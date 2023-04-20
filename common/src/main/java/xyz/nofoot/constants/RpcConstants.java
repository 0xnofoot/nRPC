package xyz.nofoot.constants;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.constants
 * @className: RpcConstants
 * @author: NoFoot
 * @date: 4/17/2023 1:26 PM
 * @description: RPC 协议中的一些宏定义
 */
public class RpcConstants {
    public static final byte[] MAGIC_NUMBER = {(byte) 'n', (byte) 'r', (byte) 'p', (byte) 'c'};
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public static final byte VERSION = 1;
    public static final byte TOTAL_LENGTH = 16;
    public static final byte REQUEST_TYPE = 1;
    public static final byte RESPONSE_TYPE = 2;

    // ping
    public static final byte HEARTBEAT_REQUEST_TYPE = 3;
    // pong
    public static final byte HEARTBEAT_RESPONSE_TYPE = 4;

    public static final int HEAD_LENGTH = 16;
    public static final String PING = "ping";
    public static final String PONG = "pong";
    public static final int MAX_FRAME_LENGTH = 8 * 1024 * 1024;
}
