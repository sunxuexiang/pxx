package com.wanmi.sbc.logisticscore.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wanmi.sbc.logisticscore.entity.WmsOrderStatus;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author lm
 * @date 2022/11/08 16:24
 */
public interface WmsOrderStatusMapper extends BaseMapper<WmsOrderStatus> {

    /**
     * 根据订单ID查询状态
     * @param tid
     * @return
     */
    WmsOrderStatus findWmsOrderStatusByTid(@Param("tid") String tid);

    List<WmsOrderStatus> findAllWmsOrderStatusByTidList(@Param("tidList")List<String> tidList);
}
