package com.wanmi.sbc.logisticscore.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wanmi.sbc.logisticscore.dao.WmsOrderStatusMapper;
import com.wanmi.sbc.logisticscore.entity.WmsOrderStatus;
import com.wanmi.sbc.logisticscore.service.WmsOrderStatusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author lm
 * @date 2022/11/08 16:26
 */
@Service
@Slf4j
@DS("oracle")
public class WmsOrderStatusServiceImpl extends ServiceImpl<WmsOrderStatusMapper,WmsOrderStatus> implements WmsOrderStatusService {

    @Autowired
    private WmsOrderStatusMapper wmsOrderStatusMapper;

    /**
     * 根据订单ID查询状态
     *
     * @param tid
     * @return
     */
    @Override
    public WmsOrderStatus findWmsOrderStatusByTid(String tid) {
        log.info("WmsOrderStatusServiceImpl.findWmsOrderStatusByTid:{}",tid);
        return wmsOrderStatusMapper.findWmsOrderStatusByTid(tid);
    }

    /**
     * 根据订单集合查询所有wms订单状态
     *
     * @param tidList
     * @return
     */
    @Override
    public List<WmsOrderStatus> findAllWmsOrderStatusByTidList(List<String> tidList) {
        return wmsOrderStatusMapper.findAllWmsOrderStatusByTidList(tidList);
    }
}
