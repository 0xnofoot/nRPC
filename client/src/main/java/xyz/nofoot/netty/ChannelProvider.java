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

    /**
     * @return: null
     * @author: NoFoot
     * @date: 4/20/2023 10:52 AM
     * @description: TODO
     */
    public ChannelProvider() {
        channelMap = new ConcurrentHashMap<>();
    }

    /**
     * @param inetSocketAddress:
     * @return: Channel
     * @author: NoFoot
     * @date: 4/20/2023 10:52 AM
     * @description: TODO
     */
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

    public void set(InetSocketAddress inetSocketAddress, Channel channel) {
        String key = inetSocketAddress.toString();
        channelMap.put(key, channel);
    }

    public void remove(InetSocketAddress inetSocketAddress) {
        String key = inetSocketAddress.toString();
        channelMap.remove(key);
        log.info("Channel Map 移除地址 [{}], Channel Map 大小 [{}]", key, channelMap.size());
    }

}
