package xyz.nofoot.netty;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.netty
 * @className: ChannelProvider
 * @author: NoFoot
 * @date: 4/19/2023 5:44 PM
 * @description: TODO
 */
@Slf4j
public class ChannelProvider {
    private final Map<String, Channel> channelMap;

    public ChannelProvider() {
        channelMap = new ConcurrentHashMap<>();
    }

    public Channel get(InetSocketAddress inetSocketAddress) {
        String key = inetSocketAddress.toString();
        if (channelMap.containsKey(key)) {
            Channel channel = channelMap.get(key);
            if (null != channel && channel.isActive()) {
                return channel;
            } else {
                log.warn("未找到 channel, 目标地址：[{}]", inetSocketAddress);
                channelMap.remove(key);
            }
        }
        return null;
    }

}
