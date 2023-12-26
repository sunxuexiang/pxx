package com.wanmi.sbc.live.host.dao;

import com.wanmi.sbc.live.api.request.host.LiveHostPageRequest;
import com.wanmi.sbc.live.host.model.root.LiveHost;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>主播Mapper</p>
 * @author 王冬明（1010331559@qq.com） Automatic Generator
 * @date 2022-09-19 11:37:24
 * @version 1.0
 * @package com.wanmi.sbc.live.host.dao
 */
@Repository
public interface LiveHostMapper {

    /**
     * 分页查询
     * @param request
     * @return
     */
    List<LiveHost> getPage(@Param("para") LiveHostPageRequest request);

    /**
     * 查询数量
     * @param request
     * @return
     */
    int getPageCount(@Param("para") LiveHostPageRequest request);

    /**
     * 获取信息
     * @param hostId
     * @return
     */
    LiveHost getInfo(Integer hostId);

    /**
     * 新增
     * @param liveHost
     * @return
     */
    int add(LiveHost liveHost);

    /**
     * 修改
     * @param liveHost
     * @return
     */
    int modify(LiveHost liveHost);
}