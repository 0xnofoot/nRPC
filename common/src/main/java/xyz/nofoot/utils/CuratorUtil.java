package xyz.nofoot.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import xyz.nofoot.enums.RpcConfigEnum;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.utils
 * @className: CuratorUtils
 * @author: NoFoot
 * @date: 4/17/2023 1:53 PM
 * @description Curator 工具类
 */
@Slf4j
public final class CuratorUtil {
    private static final int BASE_SLEEP_TIME = 1000;
    private static final int MAX_RETRIES = 3;
    public static final String ZK_REGISTER_ROOT_PATH = "/nRPC";
    private static final Map<String, List<String>> SERVICE_ADDRESS_MAP = new ConcurrentHashMap<>();
    private static final Set<String> REGISTERED_PATH_SET = ConcurrentHashMap.newKeySet();
    private static CuratorFramework zkClient;
    // 默认 zookeeper 地址，自定义地址请放在 rpc.properties 文件中
    private static final String DEFAULT_ZOOKEEPER_ADDRESS = "127.0.0.1:2181";

    /**
     * @author NoFoot
     * @date 4/17/2023 2:08 PM
     * @description 私有无参构造
     */
    private CuratorUtil() {
    }

    /**
     * @param zkClient:
     * @param path:     传入的路径
     * @author NoFoot
     * @date 4/17/2023 2:20 PM
     * @description 注册一个服务
     */
    public static void createPersistentNode(CuratorFramework zkClient, String path) {
        try {
            if (REGISTERED_PATH_SET.contains(path) || zkClient.checkExists().forPath(path) != null) {
                log.info("The node already exists. The node is:[{}]", path);
            } else {
                zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
                log.info("The node was created successfully. The node is:[{}]", path);
            }
            REGISTERED_PATH_SET.add(path);
        } catch (Exception e) {
            log.error("create persistent node for path [{}] fail", path);
            e.printStackTrace();
        }
    }

    /**
     * @param zkClient:
     * @param rpcServiceName: rpc 服务名称
     * @return List<String>
     * @author NoFoot
     * @date 4/17/2023 2:28 PM
     * @description 根据 rpc服务名称获取提供服务的 server地址
     */
    public static List<String> getChildrenNodes(CuratorFramework zkClient, String rpcServiceName) {
        if (SERVICE_ADDRESS_MAP.containsKey(rpcServiceName)) {
            return SERVICE_ADDRESS_MAP.get(rpcServiceName);
        }
        List<String> result = null;
        String servicePath = ZK_REGISTER_ROOT_PATH + "/" + rpcServiceName;
        try {
            result = zkClient.getChildren().forPath(servicePath);
            SERVICE_ADDRESS_MAP.put(rpcServiceName, result);
            registerWatcher(zkClient, rpcServiceName);
        } catch (Exception e) {
            log.error("get children nodes for path [{}] fail", servicePath);
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @param zkClient:
     * @param inetSocketAddress: 服务地址
     * @author NoFoot
     * @date 4/17/2023 2:34 PM
     * @description 根据服务地址删除对应的服务
     */
    private static void clearRegistry(CuratorFramework zkClient, InetSocketAddress inetSocketAddress) {
        REGISTERED_PATH_SET.stream().parallel().forEach(p -> {
            try {
                if (p.endsWith(inetSocketAddress.toString())) {
                    zkClient.delete().forPath(p);
                }
            } catch (Exception e) {
                log.error("clear registry for path [{}] fail", p);
                e.printStackTrace();
            }
        });
        log.info("All registry services on the server are cleared:[{}]", REGISTERED_PATH_SET);
    }

    /**
     * @return CuratorFramework
     * @author NoFoot
     * @date 4/17/2023 3:08 PM
     * @description 构造一个 zookeeper client，如果已经成功构造则直接返回该 client
     */
    public static CuratorFramework getZkClient() {
        if (zkClient != null && zkClient.getState() == CuratorFrameworkState.STARTED) {
            return zkClient;
        }

        Properties properties = PropertiesFileUtil.readPropertiesFile(RpcConfigEnum.RPC_CONFIG_PATH.getPropertyValue());
        String zookeeperAddress = properties != null
                && properties.getProperty(RpcConfigEnum.ZK_ADDRESS.getPropertyValue()) != null
                ? properties.getProperty(RpcConfigEnum.ZK_ADDRESS.getPropertyValue())
                : DEFAULT_ZOOKEEPER_ADDRESS;

        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);
        zkClient = CuratorFrameworkFactory.builder()
                .connectString(zookeeperAddress)
                .retryPolicy(retryPolicy)
                .build();
        zkClient.start();
        try {
            if (!zkClient.blockUntilConnected(30, TimeUnit.SECONDS)) {
                throw new RuntimeException("Time out waiting to connect to zookeeper");
            }
        } catch (InterruptedException e) {
            log.error("zookeeper start fail");
            e.printStackTrace();
        }

        return zkClient;
    }

    /**
     * @param zkClient:
     * @param rpcServiceName:
     * @author NoFoot
     * @date 4/17/2023 3:15 PM
     * @description 对指定的节点注册一个监听器, 当服务的子节点（即服务地址）发生变化时，更新 Map
     */
    private static void registerWatcher(CuratorFramework zkClient, String rpcServiceName) throws Exception {
        String servicePath = ZK_REGISTER_ROOT_PATH + "/" + rpcServiceName;
        PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient, servicePath, true);
        PathChildrenCacheListener pathChildrenCacheListener = (curatorFramework, pathChildrenCacheEvent) -> {
            List<String> serviceAddress = curatorFramework.getChildren().forPath(servicePath);
            SERVICE_ADDRESS_MAP.put(rpcServiceName, serviceAddress);
        };
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
        // 同步启动
        // 先把节点都拉过来，防止后面又重复触发监视器
        // 因为每一次触发监视器都会更新 Map 的数据
        // 如果采用一致性 Hash 的负载均衡策略，
        // 每一次更新 Map 都要重新计算 Hash
        // 普通启动可能会导致后续重复触发监视器，这样会导致徒增计算量，降低响应速度
        // 如果 zkClient 断开，重连也会触发此监视器
        pathChildrenCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
    }
}














