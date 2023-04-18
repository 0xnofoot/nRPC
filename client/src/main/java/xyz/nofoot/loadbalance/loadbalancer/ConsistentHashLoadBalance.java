package xyz.nofoot.loadbalance.loadbalancer;

import lombok.extern.slf4j.Slf4j;
import xyz.nofoot.dto.RpcRequest;
import xyz.nofoot.loadbalance.AbstractLoadBalance;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @projectName: nRPC
 * @package: xyz.nofoot.loadbalance.loadbalancer
 * @className: ConsistentHashLoadBalance
 * @author: NoFoot
 * @date: 4/18/23 12:14 AM
 * @description: 一致性 Hash 负载均策略
 */
@Slf4j
public class ConsistentHashLoadBalance extends AbstractLoadBalance {
    private final ConcurrentHashMap<String, ConsistentHashSelector> selectors = new ConcurrentHashMap<>();


    /**
     * @param serviceAddresses:
     * @param rpcRequest:
     * @return: String
     * @author: NoFoot
     * @date: 4/18/23 12:14 AM
     * @description: 一致性 Hash 负载均衡策略实现逻辑
     */
    @Override
    protected String doSelect(List<String> serviceAddresses, RpcRequest rpcRequest) {
        int identityHashCode = System.identityHashCode(serviceAddresses);
        String rpcServiceName = rpcRequest.getRpcServiceName();
        ConsistentHashSelector selector = selectors.get(rpcServiceName);

        // 无该服务的选择器或服务发生变动
        if (selector == null || selector.identityHashCode != identityHashCode) {
            selectors.put(rpcServiceName, new ConsistentHashSelector(serviceAddresses, 128, identityHashCode));
            selector = selectors.get(rpcServiceName);
        }
        int r = new Random().nextInt();
        // 通过stream方法本身就有随机性了，再加随机数只是增加一些随机性, 删掉随机数也行
        return selector.select(rpcServiceName + Arrays.stream(rpcRequest.getParameters()) + r);
    }

    /**
     * @projectName: nRPC
     * @package: xyz.nofoot.loadbalance.loadbalancer
     * @className: ConsistentHashLoadBalance.ConsistentHashSelector
     * @author: NoFoot
     * @date: 4/18/23 12:35 AM
     * @description: 负载均衡的具体实现类，每一个服务都有一个单独的该类实例
     */
    static class ConsistentHashSelector {
        private final TreeMap<Long, String> virtualInvokers;
        private final int identityHashCode;

        /**
         * @param invokers:         服务列表
         * @param replicaNumber:    要复制的虚拟节点数量，为 4 的整数
         * @param identityHashCode: 该对象唯一的标识，在外部可以看到由服务列表（即invokers）的 Java 对象 Hash 算法实现
         *                          本类中并没有实现服务删除的函数，如果有服务变动，客户端是通过 identityHashCode 感知的，
         *                          这个流程在外部实现，如果有服务变动，那么服务列表就会发生改变，外部计算出的 identityHashCode
         *                          也会变化，如果校验发现外部计算出的 identityHashCode 与 本类中的 identityHashCode 不一致，
         *                          那么证明服务变化了，外部会重新根据新的服务列表初始化一个并替换。
         *                          !!! 服务列表的变化是由 zookeeper 监视器实现的，
         *                          当监视到子节点事件时会重新查询子节点，并更新服务列表
         * @author: NoFoot
         * @date: 4/18/23 12:34 AM
         * @description: 构造这个一致性 Hash 选择器，对于每一个接口，都应该有一个单独的选择器，
         * 所以在外部可以看到还维护了一个 Map，用来将接口与选择器绑定
         */
        ConsistentHashSelector(List<String> invokers, int replicaNumber, int identityHashCode) {
            this.virtualInvokers = new TreeMap<>();
            this.identityHashCode = identityHashCode;

            // 计算每一个服务的虚拟节点 Hash 值
            for (String invoker : invokers) {
                for (int i = 0; i < replicaNumber / 4; i++) {
                    // 先将虚拟节点数量除以 4，计算其 md5 值
                    byte[] digest = md5(invoker + "#" + i);
                    for (int j = 0; j < 4; j++) {
                        // 根据其 md5 再计算 hash
                        // 这样做的目的是为了使得 hash 分布更均匀
                        long m = hash(digest, j);
                        // 添加服务节点
                        virtualInvokers.put(m, invoker);
                    }
                }
            }

        }

        /**
         * @param key:
         * @return: byte
         * @author: NoFoot
         * @date: 4/18/23 12:34 AM
         * @description: md5 计算逻辑，得到 128 位 字节数组（16个字节）
         */
        static byte[] md5(String key) {
            MessageDigest md;
            try {
                md = MessageDigest.getInstance("MD5");
                byte[] bytes = key.getBytes(StandardCharsets.UTF_8);
                md.update(bytes);
                return md.digest();
            } catch (NoSuchAlgorithmException e) {
                log.error("error in md5");
                e.printStackTrace();
            }

            log.error("the md5 is null");
            return new byte[]{};
        }

        /**
         * @param digest: 传入的 md5 字节数组，总共 16 个字节，所以其大小为 16
         * @param idx:    偏移量
         * @return: long
         * @author: NoFoot
         * @date: 4/18/23 12:34 AM
         * @description: hash 计算的具体逻辑，按照偏移量 idx 计算偏移，因为数组大小位 16，所有 idx最大为 3
         * 并将 byte 转换为 long，然后 &255 转换为无符号整数，
         * 最后的效果是一个 long 型无符号整数，只有低四个字节有效
         */
        static long hash(byte[] digest, int idx) {
            return ((long) (digest[3 + idx * 4] & 255) << 24
                    | (long) (digest[2 + idx * 4] & 255) << 16
                    | (long) (digest[1 + idx * 4] & 255) << 8
                    | (long) (digest[idx * 4] & 255)) & 4294967295L;
        }

        /**
         * @param rpcServiceKey: 要求对于同一个服务请求的 key是不同的
         *                       所以外部传入该参数时加上了参数和随机数
         * @return: String
         * @author: NoFoot
         * @date: 4/18/23 12:36 AM
         * @description: 外部调用的函数，传入 rpcServiceKey 作为 hash计算源
         * 因此你可以看到外部调用这个函数时传入的参数很奇怪，目的是为了保证 hash源不同
         */
        public String select(String rpcServiceKey) {
            byte[] digest = md5(rpcServiceKey);
            // 这里只指定了偏移量为 0，实际上可以维护一个值来轮流指定偏移量为 0 到 4，效果应该会更好
            return selectForKey(hash(digest, 0));
        }

        /**
         * @param hashCode:
         * @return: String
         * @author: NoFoot
         * @date: 4/18/23 12:37 AM
         * @description: 通过给定的 hashCode 环形查找最近的服务
         */
        public String selectForKey(long hashCode) {
            Map.Entry<Long, String> entry = virtualInvokers.tailMap(hashCode, true).firstEntry();

            if (entry == null) {
                entry = virtualInvokers.firstEntry();
            }

            return entry.getValue();
        }
    }
}
