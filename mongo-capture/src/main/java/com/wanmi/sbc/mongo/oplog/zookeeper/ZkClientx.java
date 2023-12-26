package com.wanmi.sbc.mongo.oplog.zookeeper;

import com.google.common.base.Function;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.I0Itec.zkclient.IZkConnection;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkException;
import org.I0Itec.zkclient.exception.ZkInterruptedException;
import org.I0Itec.zkclient.exception.ZkNoNodeException;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.apache.zookeeper.CreateMode;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * \* Created with IntelliJ IDEA.
 * \* @author: zhanggaolei
 * \* Date: 2019-12-16
 * \* Time: 14:46
 * \* To change this template use File | Settings | File Templates.
 * \* Description:
 * \
 */
public class ZkClientx extends ZkClient {

    private static Cache<String, ZkClientx> clients = CacheBuilder.newBuilder().build();

    public static ZkClientx getZkClient(String servers) {
        ZkClientx zkClientx = clients.getIfPresent(servers);
        if(zkClientx==null){
            zkClientx = new ZkClientx(servers);
            clients.put(servers,zkClientx);
        }

        return zkClientx;
    }
  /*  // 对于zkclient进行一次缓存，避免一个jvm内部使用多个zk connection
    private static LoadingCache<String, ZkClientx> clients =
            CacheBuilder.newBuilder()
                    .build(
                            new CacheLoader<String, ZkClientx>() {
                                public ZkClientx load(String servers)  {
                                    return new ZkClientx(servers);
                                }
                            });

*/
    public ZkClientx(String serverstring) {
        this(serverstring, Integer.MAX_VALUE);
    }

    public ZkClientx(String zkServers, int connectionTimeout){
        this(new ZooKeeperx(zkServers), connectionTimeout);
    }

    public ZkClientx(String zkServers, int sessionTimeout, int connectionTimeout){
        this(new ZooKeeperx(zkServers, sessionTimeout), connectionTimeout);
    }

    public ZkClientx(String zkServers, int sessionTimeout, int connectionTimeout, ZkSerializer zkSerializer){
        this(new ZooKeeperx(zkServers, sessionTimeout), connectionTimeout, zkSerializer);
    }

    private ZkClientx(IZkConnection connection, int connectionTimeout){
        this(connection, connectionTimeout, new ByteSerializer());
    }

    private ZkClientx(IZkConnection zkConnection, int connectionTimeout, ZkSerializer zkSerializer){
        super(zkConnection, connectionTimeout, zkSerializer);
    }


    public String createPersistentSequential(String path, boolean createParents) throws ZkInterruptedException,
            IllegalArgumentException, ZkException,
            RuntimeException {
        try {
            return create(path, null, CreateMode.PERSISTENT_SEQUENTIAL);
        } catch (ZkNoNodeException e) {
            if (!createParents) {
                throw e;
            }
            String parentDir = path.substring(0, path.lastIndexOf('/'));
            createPersistent(parentDir, createParents);
            return createPersistentSequential(path, createParents);
        }
    }


    public String createPersistentSequential(String path, Object data, boolean createParents)
            throws ZkInterruptedException,
            IllegalArgumentException,
            ZkException,
            RuntimeException {
        try {
            return create(path, data, CreateMode.PERSISTENT_SEQUENTIAL);
        } catch (ZkNoNodeException e) {
            if (!createParents) {
                throw e;
            }
            String parentDir = path.substring(0, path.lastIndexOf('/'));
            createPersistent(parentDir, createParents);
            return createPersistentSequential(path, data, createParents);
        }
    }


    public void createPersistent(String path, Object data, boolean createParents) throws ZkInterruptedException,
            IllegalArgumentException, ZkException,
            RuntimeException {
        try {
            create(path, data, CreateMode.PERSISTENT);
        } catch (ZkNodeExistsException e) {
            if (!createParents) {
                throw e;
            }
        } catch (ZkNoNodeException e) {
            if (!createParents) {
                throw e;
            }
            String parentDir = path.substring(0, path.lastIndexOf('/'));
            createPersistent(parentDir, createParents);
            createPersistent(path, data, createParents);
        }
    }
}
