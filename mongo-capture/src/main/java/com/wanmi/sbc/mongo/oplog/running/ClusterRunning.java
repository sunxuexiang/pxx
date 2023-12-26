package com.wanmi.sbc.mongo.oplog.running;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.mongo.oplog.comm.AbstractMongoCaptureLifeCycle;
import com.wanmi.sbc.mongo.oplog.context.MongoCaptureAdapter;
import com.wanmi.sbc.mongo.oplog.data.Parameter;
import com.wanmi.sbc.mongo.oplog.data.RunningData;
import com.wanmi.sbc.mongo.oplog.utils.AddressUtils;
import com.wanmi.sbc.mongo.oplog.utils.BooleanMutex;
import com.wanmi.sbc.mongo.oplog.zookeeper.ZkClientx;
import lombok.Data;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.exception.ZkException;
import org.I0Itec.zkclient.exception.ZkInterruptedException;
import org.I0Itec.zkclient.exception.ZkNoNodeException;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;
import java.text.MessageFormat;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * \* Created with IntelliJ IDEA.
 * \* @author: zhanggaolei
 * \* Date: 2019-12-18
 * \* Time: 14:08
 * \* To change this template use File | Settings | File Templates.
 * \* Description:
 * \    集群模式运行
 */
@Data
@Component
public class ClusterRunning extends AbstractMongoCaptureLifeCycle {
    private static final Logger log = LoggerFactory.getLogger(ClusterRunning.class);
    private ZkClientx                   zkClientx;
    private String                      destination;
    private IZkDataListener             dataListener;
    private BooleanMutex                mutex                   = new BooleanMutex(false);
    private RunningData                 runningData;
    private volatile boolean            running                 = false;
    private volatile boolean            release                 = false;
    private RunningData                 activeData;
    private int                         delayTime               = 5;
    private ScheduledExecutorService    delayExecutor           = Executors.newScheduledThreadPool(1);
    @Autowired
    private MongoCaptureAdapter mongoCaptureAdapter;
    public ClusterRunning(){

    }
    public void initListener(){
        dataListener = new IZkDataListener() {

            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {
                RunningData runningData = JSONObject.parseObject((byte[]) data, RunningData.class);
                if (!isMine(runningData.getAddress())) {
                    mutex.set(false);
                }

                if (!runningData.isActive() && isMine(runningData.getAddress())) { // 说明出现了主动释放的操作，并且本机之前是active
                    release = true;
                    releaseRunning();// 彻底释放mainstem
                }

                activeData = runningData;
            }

            @Override
            public void handleDataDeleted(String dataPath) throws Exception {
                mutex.set(false);
                // 触发一下退出,可能是人为干预的释放操作或者网络闪断引起的session expired timeout
                processActiveExit();
                if (!release && activeData != null && isMine(activeData.getAddress())) {
                    // 如果上一次active的状态就是本机，则即时触发一下active抢占
                    initRunning();
                } else {
                    // 否则就是等待delayTime，避免因网络瞬端或者zk异常，导致出现频繁的切换操作
                    delayExecutor.schedule(new Runnable() {

                        @Override
                        public void run() {
                            initRunning();
                        }
                    }, delayTime, TimeUnit.SECONDS);
                }
            }

        };
    }
    public synchronized void initRunning() {
        if (!isStart()) {
            return;
        }

        String path = Parameter.getRunningPath(this.destination);
        // 序列化
        byte[] bytes = JSONObject.toJSONBytes(runningData);
        try {
            mutex.set(false);
            zkClientx.create(path, bytes, CreateMode.EPHEMERAL);
            processActiveEnter();// 触发一下事件
            activeData = runningData;
            mutex.set(true);
        } catch (ZkNodeExistsException e) {
            bytes = zkClientx.readData(path, true);
            if (bytes == null) {// 如果不存在节点，立即尝试一次
                initRunning();
            } else {
                activeData = JSONObject.parseObject(bytes, RunningData.class);
                // 如果发现已经存在,判断一下是否自己,避免活锁
                if (isMine(activeData.getAddress())) {
                    mutex.set(true);
                }
            }
            log.info("##############this client standby node################");
        } catch (ZkNoNodeException e) {
            zkClientx.createPersistent(Parameter.getRootPath(this.destination),
                    true); // 尝试创建父节点
            initRunning();
        } catch (Throwable t) {
            log.error(MessageFormat.format("There is an error when execute initRunning method, with destination [{0}].",
                    destination),
                    t);
            // 出现任何异常尝试release
            releaseRunning();
            throw new RuntimeException("something goes wrong in initRunning method. ", t);
        }
    }

    @Override
    public void start() {
        super.start();
        initListener();
        String path = Parameter.getRunningPath(this.destination);
        zkClientx.subscribeDataChanges(path, dataListener);
        initRunning();
    }



    @Override
    public void stop() {
        super.stop();

        String path = Parameter.getRunningPath(this.destination);
        zkClientx.unsubscribeDataChanges(path, dataListener);
        releaseRunning(); // 尝试一下release
        if (delayExecutor != null) {
            delayExecutor.shutdown();
        }
    }
    public boolean isMine(String address){
        return this.runningData.getAddress().equals(address);
    }

    /**
     * 检查当前的状态
     */
    public boolean check() {
        String path = Parameter.getRunningPath(this.destination);
        try {
            byte[] bytes = zkClientx.readData(path);
            RunningData eventData = JSONObject.parseObject(bytes, RunningData.class);
            activeData = eventData;// 更新下为最新值
            // 检查下nid是否为自己
            boolean result = isMine(activeData.getAddress());
            if (!result) {
                log.warn("mongoCapture is running in [{}] , but not in [{}]",
                        activeData.getAddress(),
                        runningData.getAddress());
            }
            return result;
        } catch (ZkNoNodeException e) {
            log.warn("mongoCapture is not run any in node");
            return false;
        } catch (ZkInterruptedException e) {
            log.warn("mongoCapture check is interrupt");
            Thread.interrupted();// 清除interrupt标记
            return check();
        } catch (ZkException e) {
            log.warn("mongoCapture check is failed");
            return false;
        }
    }

    public boolean releaseRunning() {
        if (check()) {
            String path = Parameter.getRunningPath(this.destination);
            zkClientx.delete(path);
            mutex.set(false);
            processActiveExit();
            return true;
        }

        return false;
    }

    private void processActiveEnter() {

        this.runningData.setAddress(AddressUtils.getHostIp());

        String path = Parameter.getRunningPath(this.destination);
        // 序列化
        byte[] bytes = JSONObject.toJSONBytes(runningData);
        zkClientx.writeData(path, bytes);
        this.mongoCaptureAdapter.setZkClientx(zkClientx);
  //      this.mongoCaptureAdapter.clearMemoryPosition();
        this.mongoCaptureAdapter.start();
    }

    private void processActiveExit() {
        this.mongoCaptureAdapter.stop();
    }
}
