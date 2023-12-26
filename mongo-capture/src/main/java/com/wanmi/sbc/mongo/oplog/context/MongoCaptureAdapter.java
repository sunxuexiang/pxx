package com.wanmi.sbc.mongo.oplog.context;

import com.mongodb.*;
import com.wanmi.sbc.mongo.oplog.comm.AbstractMongoCaptureLifeCycle;
import com.wanmi.sbc.mongo.oplog.context.handler.*;
import com.wanmi.sbc.mongo.oplog.data.Parameter;
import com.wanmi.sbc.mongo.oplog.data.Position;
import com.wanmi.sbc.mongo.oplog.comm.factory.MongoFactory;
import com.wanmi.sbc.mongo.oplog.data.OplogData;
import com.wanmi.sbc.mongo.oplog.utils.TimestampUtil;
import com.wanmi.sbc.mongo.oplog.zookeeper.ZkClientx;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.bson.types.BSONTimestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 获取oplog执行器
 * @Author: ZhangLingKe
 * @CreateDate: 2019/8/14 17:33
 */
@Slf4j
@Component
public class MongoCaptureAdapter extends AbstractMongoCaptureLifeCycle {

    @Autowired
    private OplogPullHandler oplogCursorHandler;

    @Autowired
    private OplogUnUseDataFilterHandler oplogUnUseDataFilterHandler;

    @Autowired
    private OplogRegexFilterHandler oplogRegexFilterHandler;

    @Autowired
    private OplogParserHandler oplogParserHandler;

    @Autowired
    private OplogSendHandler oplogSendHandler;

    @Autowired
    private OplogPositionHandler oplogPositionHandler;

    @Autowired
    private MongoFactory factory;

    private Thread thread;
    private ZkClientx zkClientx;

    public MongoCaptureAdapter() {

    }

    @Override
    public void start() {
        this.oplogPositionHandler.setZkClientx(this.zkClientx);
        this.oplogPositionHandler.start();
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                process();
            }
        });
        thread.start();
        log.info("*************start successful****************");
    }

    @Override
    public void stop() {
        if (thread != null) {
            try {
                thread.interrupt();
                thread.join();
            } catch (MongoInterruptedException e) {
                log.info("mongo connection pool stop");
            } catch (Exception e) {
                log.info("process thread stop");
            }

        }
        if (oplogPositionHandler != null) {
            oplogPositionHandler.stop();
        }

        log.info("*************stop successful****************");
    }

    public void clearMemoryPosition() {
        this.oplogPositionHandler.getPositionManager().removeCursor();
    }

    /**
     * 主线程
     */
    public void process() {
        while (!thread.isInterrupted()) {
            try {
                Position position = oplogPositionHandler.getPositionManager().getCursor();
                if (position == null) {
                    position = new Position(oplogPositionHandler.getClientConfig().getMetaMode().name()
                            , Parameter.getPositionPath(oplogPositionHandler.getClientConfig().getDestination()));
                    position.setPosition(getTimestamp());
                }
                if (position.getPosition() == null) {
                    position.setPosition(getTimestamp());
                }
                if (position.getClusterName() == null) {
                    position.setClusterName(oplogPositionHandler.getClientConfig().getClusterName());
                }
                if (position.getModel() == null) {
                    position.setModel(oplogPositionHandler.getClientConfig().getMetaMode().name());
                }

                // 获取 Oplog 数据
                DBCursor dbCursor = oplogCursorHandler.oplogCursor(position.getPosition());

                while (dbCursor.hasNext()) {
                    DBObject dbObject = dbCursor.next();
                    // 过滤 Oplog 数据
                    if (!oplogUnUseDataFilterHandler.shouldSkip(dbObject)) {
                        List<Map<String, Object>> list = new ArrayList<>();
                        Map<String, Object> map = dbObject.toMap();

                        if (oplogUnUseDataFilterHandler.isTransaction(map)) {
                            BasicDBObject basicDBObject = (BasicDBObject) map.get("o");
                            list = (List) basicDBObject.get("applyOps");

                        } else {
                            list.add(map);
                        }
                        if (CollectionUtils.isNotEmpty(list)) {
                            for (Map<String, Object> temp : list) {
                                String ns = (String) temp.get("ns");
                                if (oplogRegexFilterHandler.filter(ns)) {

                                    // 解析 Oplog 数据
                                    OplogData oplogData = oplogParserHandler.process(temp);

                                    // 使用解析后的数据
                                    oplogSendHandler.send(oplogData);
                                }
                            }
                        }
                    }
                    // 更新时间
                    position.setPosition((BSONTimestamp) dbObject.get("ts"));
                    oplogPositionHandler.getPositionManager().updateCursor(position);
                }
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (MongoException e) {
                log.error(e.getMessage());
                try {
                    TimeUnit.MILLISECONDS.sleep(5000);
                } catch (Exception e1) {
                    log.error("process sleep error:", e);
                }
                continue;

            } catch (Throwable t) {
                log.error("process error", t);
                break;
            }
        }
    }

    /**
     * 获取时间戳
     */
    private BSONTimestamp getTimestamp() {

        //1.获取最近一次保存的值
        BSONTimestamp timestamp = TimestampUtil.get();

        //2.从mongodb中获取最新的值
        if (timestamp == null) {
            DBObject sort = new BasicDBObject("$natural", -1);
            try (DBCursor cursor = factory.getDBCollection().find().sort(sort).limit(1)) {
                while (cursor.hasNext()) {
                    DBObject dbObject = cursor.next();
                    timestamp = (BSONTimestamp) dbObject.get("ts");
                    break;
                }
            }
        }

        //3.仍获取不到则以当前时刻作为起始值
        if (timestamp == null) {
            timestamp = TimestampUtil.getNow();
        }
        return timestamp;
    }

    public void setZkClientx(ZkClientx zkClientx) {
        this.zkClientx = zkClientx;
    }
}
