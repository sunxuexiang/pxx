package com.wanmi.sbc.mongo.oplog.position;

import com.google.common.collect.MapMaker;
import com.wanmi.sbc.mongo.oplog.comm.AbstractMongoCaptureLifeCycle;
import com.wanmi.sbc.mongo.oplog.data.Position;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * \* Created with IntelliJ IDEA.
 * \* @author: zhanggaolei
 * \* @date: 2019-12-17
 * \* @time: 15:15
 * \* To change this template use File | Settings | File Templates.
 * \* @description:
 * \
 */
@Data
public class MemoryPositionManager extends AbstractMongoCaptureLifeCycle implements PositionManager {
    protected Map<String, Position> cursors;
    protected String destination = "default";
    protected Integer period;

    @Override
    public void start(){
        cursors = new MapMaker().makeMap();
    }
    @Override
    public void stop(){
        cursors.clear();
    }
    @Override
    public Position getCursor(){
        if(cursors==null){
            return null;
        }
        return cursors.get(destination);
    }

    @Override
    public void updateCursor(Position position) {
        if(position!=null) {
            position.setDateTime(LocalDateTime.now());
            cursors.put(destination,position);
        }

    }

    @Override
    public void removeCursor() {
        if(cursors!=null) {
            cursors.clear();
        }
    }
}
