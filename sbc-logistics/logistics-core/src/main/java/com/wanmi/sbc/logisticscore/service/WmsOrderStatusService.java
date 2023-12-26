package com.wanmi.sbc.logisticscore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wanmi.sbc.logisticscore.entity.WmsOrderStatus;

import java.util.List;

/**
 * @author lm
 * @date 2022/11/08 16:26
 */
public interface WmsOrderStatusService  extends IService<WmsOrderStatus> {

    /**
     * 根据订单ID查询状态
     * @param tid
     * @return
     */
    WmsOrderStatus findWmsOrderStatusByTid(String tid);

    /**
     * 根据订单集合查询所有wms订单状态
     * @param tidList
     * @return
     */
    List<WmsOrderStatus> findAllWmsOrderStatusByTidList(List<String> tidList);
}
