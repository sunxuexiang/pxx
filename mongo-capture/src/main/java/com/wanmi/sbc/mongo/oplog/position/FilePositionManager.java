package com.wanmi.sbc.mongo.oplog.position;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Sets;
import com.wanmi.sbc.mongo.oplog.data.Position;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * \* Created with IntelliJ IDEA.
 * \* @author: zhanggaolei
 * \* @date: 2019-12-13
 * \* @time: 17:45
 * \* To change this template use File | Settings | File Templates.
 * \* @description:
 * \ 文件存储定位信息
 */
@Data
public class FilePositionManager extends MemoryPositionManager implements PositionManager {

    private static final Logger log = LoggerFactory.getLogger(FilePositionManager.class);
    private static final String FILE_NAME = "mate.data";
    private File dataDir;

    private ScheduledExecutorService executor;
    private Set<String> updateSet;

    @Override
    public void start() {
        super.start();
        if(super.getCursor() == null){
            super.updateCursor(this.loadDataFromFile());
        }
        updateSet = Sets.newConcurrentHashSet();
        executor = Executors.newScheduledThreadPool(1);
        // 启动定时工作任务
        executor.scheduleAtFixedRate(
                new Runnable() {
                    @Override
                    public void run() {
                        if(CollectionUtils.isNotEmpty(updateSet)) {
                            Position position = FilePositionManager.super.getCursor();
                            try {

                                if (!Objects.isNull(position)) {
                                    flushDataToFile(position);
                                }
                            } catch (Throwable e) {
                                log.error("period update" + JSONObject.toJSONString(position) + " curosr failed!", e);
                            }
                            updateSet.remove(destination);
                        }
                    }
                },
                period,
                period,
                TimeUnit.MILLISECONDS);
    }

    @Override
    public void stop() {

        executor.shutdown();
        flushDataToFile(getCursor());
        super.stop();
    }

    @Override
    public Position getCursor() {

        Position position = super.getCursor();
        if(position==null || position.getPosition()==null) {
            position = loadDataFromFile();
        }

        return position;
    }

    @Override
    public void updateCursor( Position position) {
        updateSet.add(destination);
        super.updateCursor(position);
    }

    @Override
    public void removeCursor() {
        super.removeCursor();
    }

    public void setDataDir(String dataDir) {
        this.dataDir = new File(dataDir);
    }

    private File getDataFile() {

        if (!dataDir.exists()) {
            try {
                FileUtils.forceMkdir(dataDir);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return new File(dataDir, FILE_NAME);
    }

    private Position loadDataFromFile(){
        Position position = new Position();
        File file = getDataFile();
        try {
            if (!file.exists()) {
                return null;
            }

            String json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            position = Position.jsonToBean(json);
            return position;

        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }
   private void flushDataToFile(Position position){
       File file = getDataFile();
       try {
           FileUtils.writeStringToFile(file, JSONObject.toJSONString(position));
       } catch (IOException ioe) {
           throw new RuntimeException(ioe);
       }
   }


}
