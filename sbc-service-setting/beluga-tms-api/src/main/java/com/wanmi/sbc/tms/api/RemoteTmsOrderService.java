package com.wanmi.sbc.tms.api;


import com.wanmi.sbc.tms.api.domain.R;
import com.wanmi.sbc.tms.api.domain.dto.TmsOrderAmountDTO;
import com.wanmi.sbc.tms.api.domain.dto.TmsOrderThirdDeliveryDTO;
import com.wanmi.sbc.tms.api.domain.vo.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * TMS大众物流remote服务
 */
@FeignClient(value = "beluga-tms", url = "${feign.url.tms:#{null}}", contextId = "remoteTmsOrderService")
public interface RemoteTmsOrderService {

    /**
     * 计算配送到店的物流费用（单商家）
     */
    @PostMapping(value = "/order/calculate", headers = {"from-source=inner"})
    R<TmsOrderAmountDTO> calculateTmsAmount(@RequestBody TmsOrderAreaVO areaVO);

    /**
     * 计算配送到店的物流费用（多商家凑门槛）
     */
    @PostMapping(value = "/order/calculateMutli", headers = {"from-source=inner"})
    R<List<TmsOrderAmountDTO>> calculateTmsAmountMutli(@RequestBody TmsOrderAreaVO areaVO);


    /**
     * 创建TMS承运单(付款)
     */
    @PostMapping(value = "/order/create", headers = {"from-source=inner"})
    R<List<TmsOrderSaveResponseVO>> createTmsOrder(@RequestBody TmsOrderBatchWrapSaveVO saveVO);

    /**
     * 商家发货
     */
    @PostMapping(value = "/order/update-status/trade-supplier-deliver", headers = {"from-source=inner"})
    R<TmsOrderThirdDeliveryDTO> updateStatusForTradeSupplierDeliver(@RequestBody TmsOrderTradeStatusUpdateVO vo);


    /**
     * 用户签收
     */
    @PostMapping(value = "/order/update-status/trade-buyer-receive", headers = {"from-source=inner"})
    R<Boolean> updateStatusForTradeBuyerReceive(@RequestBody TmsOrderTradeStatusUpdateVO vo);


    /**
     * 取消订单
     */
    @PostMapping(value = "/order/update-status/trade-cancel", headers = {"from-source=inner"})
    R<Boolean> updateStatusForTradeCancel(@RequestBody TmsOrderTradeStatusUpdateVO vo);

    /**
     * 获取商家缺货发货后的运单退款金额
     */
    @GetMapping(value = "/order/getRefundAmountByTradeId/{tradeId}", headers = {"from-source=inner"})
    R<Double> getRefundAmountByTradeId(@PathVariable("tradeId") String tradeId);

    /**
     * 获取运单详情
     */
    @GetMapping(value = "/order/getInfo/{tradeId}", headers = {"from-source=inner"})
    R<TmsOrderVO> getInfo(@PathVariable("tradeId") String tradeId);

    /**
     * 承运商商家入驻
     */
    @PostMapping(value = "/user/register", headers = {"from-source=inner"})
    R<Boolean> registerUserInfo(@RequestBody SysUser vo);

    /**
     * 获取配送到店运费模板描述
     */
    @PostMapping(value = "/order/getTmsFreightTempDesc", headers = {"from-source=inner"})
    R<String> getTmsFreightTempDesc(@RequestBody TmsOrderAreaVO areaVO);
}
