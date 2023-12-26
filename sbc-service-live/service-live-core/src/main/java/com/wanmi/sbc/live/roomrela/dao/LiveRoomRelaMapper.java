package com.wanmi.sbc.live.roomrela.dao;

import com.wanmi.sbc.live.api.request.roomrela.LiveRoomRelaPageRequest;
import com.wanmi.sbc.live.roomrela.model.root.LiveAccountNum;
import com.wanmi.sbc.live.roomrela.model.root.LiveRoomRela;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>直播间关联Mapper</p>
 * @author liudan 刘丹（ldalc.com） Automatic Generator
 * @date 2022-09-16 19:58:39
 * @version 1.0
 * @package com.wanmi.sbc.live.room_rela.dao
 */
@Repository
public interface LiveRoomRelaMapper {

    /**
     * 分页查询
     * @param request
     * @return
     */
    List<LiveRoomRela> getPage(LiveRoomRelaPageRequest request);

    /**
     * 查询数量
     * @param request
     * @return
     */
    int getPageCount(LiveRoomRelaPageRequest request);

    /**
     * 获取信息
     * @param room_relaId
     * @return
     */
    LiveRoomRela getInfo(Long room_relaId);

    /**
     * 新增
     * @param request
     * @return
     */
    int add(LiveRoomRela request);

    /**
     * 修改
     * @param request
     * @return
     */
    int modify(LiveRoomRela request);

    /**
     * 删除
     *  @return返回值为删除的条数
     */
    long delete(Long id);

    /**
     * 删除
     *  @return返回值为删除的条数
     */
    long deleteByRoom(LiveRoomRela request);

    List<LiveAccountNum> countByLiveAccount(@Param("customerIds") List<String> customerIds, @Param("relaType") String relaType);
}