package xyz.nofoot.loadbalance.loadbalancer;

import lombok.extern.slf4j.Slf4j;
import xyz.nofoot.dto.RpcRequest;
import xyz.nofoot.loadbalance.AbstractLoadBalance;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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
     * @return String
     * @author NoFoot
     * @date 4/18/23 12:14 AM
     * @description 一致性 Hash 负载均衡策略实现逻辑
     */
    @Override

    protected String doSelect(List<String> serviceAddresses, RpcRequest rpcRequest) {
        int identityHashCode = System.identityHashCode(serviceAddresses);
        String rpcServiceName = rpcRequest.getRpcServiceName();
        ConsistentHashSelector selector = selectors.get(rpcServiceName);

        if (selector == null || selector.identityHashCode != identityHashCode) {
            selectors.put(rpcServiceName, new ConsistentHashSelector(serviceAddresses, 160, identityHashCode));
            selectors.get(rpcServiceName);
        }
        return selector.select(rpcServiceName + Arrays.stream(rpcRequest.getParameters()));
    }

    /**
     * @projectName: nRPC
     * @package: xyz.nofoot.loadbalance.loadbalancer
     * @className: ConsistentHashLoadBalance.ConsistentHashSelector
     * @author: NoFoot
     * @date 4/18/23 12:35 AM
     * @description TODO
     */
    static class ConsistentHashSelector {
        private final TreeMap<Long, String> virtualInvokers;
        private final int identityHashCode;

        /**
         * @param invokers:
         * @param replicaNumber:
         * @param identityHashCode:
         * @author NoFoot
         * @date 4/18/23 12:34 AM
         * @description TODO
         */
        ConsistentHashSelector(List<String> invokers, int replicaNumber, int identityHashCode) {
            this.virtualInvokers = new TreeMap<>();
            this.identityHashCode = identityHashCode;

            for (String invoker : invokers) {
                for (int i = 0; i < replicaNumber / 4; i++) {
                    byte[] digest = md5(invoker + i);
                    for (int j = 0; j < 4; j++) {
                        long m = hash(digest, j);
                        virtualInvokers.put(m, invoker);
                    }
                }
            }

        }

        /**
         * @param key:
         * @return byte
         * @author NoFoot
         * @date 4/18/23 12:34 AM
         * @description TODO
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
         * @param digest:
         * @param idx:
         * @return long
         * @author NoFoot
         * @date 4/18/23 12:34 AM
         * @description TODO
         */
        static long hash(byte[] digest, int idx) {
            return ((long) (digest[3 + idx * 4] & 255) << 24
                    | (long) (digest[2 + idx * 4] & 255) << 16
                    | (long) (digest[1 + idx * 4] & 255) << 8
                    | (long) (digest[idx * 4] & 255)) & 4294967295L;
        }

        /**
         * @param rpcServiceKey:
         * @return String
         * @author NoFoot
         * @date 4/18/23 12:36 AM
         * @description TODO
         */
        public String select(String rpcServiceKey) {
            byte[] digest = md5(rpcServiceKey);
            return selectForKey(hash(digest, 0));
        }

        /**
         * @param hashCode:
         * @return String
         * @author NoFoot
         * @date 4/18/23 12:37 AM
         * @description TODO
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
