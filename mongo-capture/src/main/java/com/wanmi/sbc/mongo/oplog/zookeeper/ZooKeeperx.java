package com.wanmi.sbc.mongo.oplog.zookeeper;

import org.I0Itec.zkclient.ZkConnection;
import org.I0Itec.zkclient.exception.ZkException;
import org.apache.zookeeper.ClientCnxn;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.client.ConnectStringParser;
import org.apache.zookeeper.client.HostProvider;
import org.apache.zookeeper.client.StaticHostProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 封装了ZooKeeper，使其支持节点的优先顺序
 */
public class ZooKeeperx extends ZkConnection {

    private static final String SERVER_COMMA            = ";";
    private static final Logger logger                  = LoggerFactory.getLogger(ZooKeeperx.class);
    private static final Field CLIENT_CNXN_FIELD        = ReflectionUtils.findField(ZooKeeper.class, "cnxn");
    private static final Field HOST_PROVIDER_FIELD      = ReflectionUtils.findField(ClientCnxn.class, "hostProvider");
    private static final Field SERVER_ADDRESSES_FIELD   = ReflectionUtils.findField(StaticHostProvider.class,
                                                            "serverAddresses");
    private static final Field ZOOKEEPER_LOCK_FIELD     = ReflectionUtils.findField(ZkConnection.class,
                                                            "_zookeeperLock");
    private static final Field ZOOKEEPER_FILED          = ReflectionUtils.findField(ZkConnection.class, "_zk");
    private static final int    DEFAULT_SESSION_TIMEOUT = 90000;

    private final List<String>  _serversList;
    private final int           _sessionTimeOut;

    public ZooKeeperx(String zkServers){
        this(zkServers, DEFAULT_SESSION_TIMEOUT);
    }

    public ZooKeeperx(String zkServers, int sessionTimeOut){
        super(zkServers, sessionTimeOut);
        _serversList = Arrays.asList(this.getServers().split(SERVER_COMMA));
        _sessionTimeOut = sessionTimeOut;
    }

    @Override
    public void connect(Watcher watcher) {
        ReflectionUtils.makeAccessible(ZOOKEEPER_LOCK_FIELD);
        ReflectionUtils.makeAccessible(ZOOKEEPER_FILED);
        Lock _zookeeperLock = (ReentrantLock) ReflectionUtils.getField(ZOOKEEPER_LOCK_FIELD, this);
        ZooKeeper _zk = (ZooKeeper) ReflectionUtils.getField(ZOOKEEPER_FILED, this);

        _zookeeperLock.lock();
        try {
            if (_zk != null) {
                throw new IllegalStateException("zk client has already been started");
            }
            String zkServers = _serversList.get(0);

            try {
                logger.debug("Creating new ZookKeeper instance to connect to " + zkServers + ".");
                _zk = new ZooKeeper(zkServers, _sessionTimeOut, watcher);
                configMutliCluster(_zk);
                ReflectionUtils.setField(ZOOKEEPER_FILED, this, _zk);
            } catch (IOException e) {
                throw new ZkException("Unable to connect to " + zkServers, e);
            }
        } finally {
            _zookeeperLock.unlock();
        }
    }

    // ===============================

    public void configMutliCluster(ZooKeeper zk) {
        if (_serversList.size() == 1) {
            return;
        }
        String cluster1 = _serversList.get(0);
        try {
            if (_serversList.size() > 1) {
                // 强制的声明accessible
                ReflectionUtils.makeAccessible(CLIENT_CNXN_FIELD);
                ReflectionUtils.makeAccessible(HOST_PROVIDER_FIELD);
                ReflectionUtils.makeAccessible(SERVER_ADDRESSES_FIELD);

                // 添加第二组集群列表
                for (int i = 1; i < _serversList.size(); i++) {
                    String cluster = _serversList.get(i);
                    // 强制获取zk中的地址信息
                    ClientCnxn cnxn = (ClientCnxn) ReflectionUtils.getField(CLIENT_CNXN_FIELD, zk);
                    HostProvider hostProvider = (HostProvider) ReflectionUtils.getField(HOST_PROVIDER_FIELD, cnxn);
                    List<InetSocketAddress> serverAddrs = (List<InetSocketAddress>) ReflectionUtils.getField(SERVER_ADDRESSES_FIELD,
                        hostProvider);
                    // 添加第二组集群列表
                    serverAddrs.addAll(new ConnectStringParser(cluster).getServerAddresses());
                }
            }
        } catch (Exception e) {
            try {
                if (zk != null) {
                    zk.close();
                }
            } catch (InterruptedException ie) {
                logger.info("zookeeper thread interrupt");
                Thread.currentThread().interrupt();
                // ignore interrupt
            }
            throw new ZkException("zookeeper_create_error, serveraddrs=" + cluster1, e);
        }

    }
}
