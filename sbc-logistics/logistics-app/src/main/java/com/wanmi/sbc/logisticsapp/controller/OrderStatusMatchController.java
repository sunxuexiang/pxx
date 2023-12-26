package com.wanmi.sbc.logisticsapp.controller;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.logisticsbean.dto.WmsOrderStatusTidDTO;
import com.wanmi.sbc.logisticsbean.dto.WmsOrderStatusTidListDTO;
import com.wanmi.sbc.logisticsbean.vo.WmsOrderStatusVo;
import com.wanmi.sbc.logisticscore.entity.WmsOrderStatus;
import com.wanmi.sbc.logisticscore.service.WmsOrderStatusService;
import com.wanmi.sbc.provider.OrderStatusMatchProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author lm
 * @date 2022/11/08 16:13
 */
@RestController
@Slf4j
@Validated
public class OrderStatusMatchController implements OrderStatusMatchProvider {


    @Autowired
    private WmsOrderStatusService orderStatusService;

    /**
     * 根据订单ID查询wms的订单状态
     * @param request
     * @return
     */
    @Override
    public BaseResponse<WmsOrderStatusVo> queryWmsOrderStatusByTid(WmsOrderStatusTidDTO request) {
        log.info("OrderStatusMatchController.queryWmsOrderStatusByTid:{}", JSON.toJSONString(request));
        WmsOrderStatus wmsOrderStatus = orderStatusService.findWmsOrderStatusByTid(request.getTid());
        WmsOrderStatusVo convert =BeanUtil.copyProperties(wmsOrderStatus,WmsOrderStatusVo.class);
        return BaseResponse.success(convert);
    }

    @Override
    public BaseResponse<List<WmsOrderStatusVo>> queryAllWmsOrderStatusByTidList(WmsOrderStatusTidListDTO request) {
        log.info("OrderStatusMatchController.queryAllWmsOrderStatusByTidList:{}", JSON.toJSONString(request));
        List<WmsOrderStatus> wmsOrderStatusVos = orderStatusService.findAllWmsOrderStatusByTidList(request.getTidList());
        return BaseResponse.success(BeanUtil.copyToList(wmsOrderStatusVos,WmsOrderStatusVo.class));
    }
}
