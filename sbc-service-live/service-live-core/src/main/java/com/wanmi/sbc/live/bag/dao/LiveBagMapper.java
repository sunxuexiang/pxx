package com.wanmi.sbc.live.bag.dao;

import com.wanmi.sbc.live.api.request.bag.LiveBagAddRequest;
import com.wanmi.sbc.live.api.request.bag.LiveBagListRequest;
import com.wanmi.sbc.live.api.request.bag.LiveBagModifyRequest;
import com.wanmi.sbc.live.api.request.bag.LiveBagPageRequest;
import com.wanmi.sbc.live.bag.model.root.LiveBag;
import com.wanmi.sbc.live.bean.vo.LiveBagVO;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * <p>福袋Mapper</p>
 * @author 刘丹（ldalc.com） Automatic Generator
 * @date 2022-09-20 13:04:21
 * @version 1.0
 * @package com.wanmi.sbc.live.bag.dao
 */
@Repository
public interface LiveBagMapper {

    /**
     * 分页查询
     * @param request
     * @return
     */
    List<LiveBag> getPage(@Param("para") LiveBagPageRequest request);

    /**
     * 查询数量
     * @param request
     * @return
     */
    int getPageCount(@Param("para") LiveBagPageRequest request);

    /**
     * 获取信息
     * @param bagId
     * @return
     */
    LiveBag getInfo(Long bagId);

    /**
     * 新增
     * @param request
     * @return
     */
    int add(LiveBag request);

    /**
     * 修改
     * @param request
     * @return
     */
    int modify(LiveBag request);

    /**|
     * 通过直播记录id获取福袋列表
     * @param request
     * @return
     */
    List<LiveBagVO> getListBagByLiveId(LiveBagListRequest request);

    /**
     * 直播间获取福袋列表
     * @param liveRoomId
     * @return
     */
    List<LiveBagVO> getListBagByLiveRoomId(Integer liveRoomId);
}