package com.wanmi.sbc.tms.api;


import com.wanmi.sbc.tms.api.domain.R;
import com.wanmi.sbc.tms.api.domain.dto.ExpressOrderAmountDTO;
import com.wanmi.sbc.tms.api.domain.dto.ExpressOrderDTO;
import com.wanmi.sbc.tms.api.domain.dto.ExpressOrderThirdDeliveryDTO;
import com.wanmi.sbc.tms.api.domain.dto.ExpressSaveDTO;
import com.wanmi.sbc.tms.api.domain.vo.ExpressOrderAreaVO;
import com.wanmi.sbc.tms.api.domain.vo.ExpressOrderDeliverVO;
import com.wanmi.sbc.tms.api.domain.vo.ExpressSaveVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 快递到家remote运单服务
 * @author jkp
 * @date 2023-11-11
 **/
@FeignClient(contextId = "remoteExpressOrderService", value = "beluga-tms")
public interface RemoteExpressOrderService {

    /**
     * 计算配送到店的物流费用（多商家）
     */
    @PostMapping(value = "/remote/expressOrder/calculateMutli",headers = {"from-source=inner"})
    R<List<ExpressOrderAmountDTO>> calculateMutli(@RequestBody ExpressOrderAreaVO areaVO);

    /**
     * 创建TMS承运单(付款)
     *
     * @param
     * @return
     */
    @PostMapping(value = "/remote/expressOrder/create",headers = {"from-source=inner"})
    R<ExpressSaveDTO> createExpressOrder(@RequestBody ExpressSaveVO saveVO);

    /**
     * 商家发货
     */
    @PostMapping(value = "/remote/expressOrder/updateTradeSupplierDeliver",headers = {"from-source=inner"})
    R<ExpressOrderThirdDeliveryDTO> updateTradeSupplierDeliver(@RequestBody ExpressOrderDeliverVO vo);


    /**
     * 用户签收
     */
    @PostMapping(value = "/remote/expressOrder/updateTradeBuyerReceive",headers = {"from-source=inner"})
    R<Boolean> updateTradeBuyerReceive(@RequestBody ExpressOrderDeliverVO vo);


    /**
     * 取消订单
     */
    @PostMapping(value = "/remote/expressOrder/updateTradeCancel",headers = {"from-source=inner"})
    R<Boolean> updateTradeCancel(@RequestBody ExpressOrderDeliverVO vo);


    /**
     * 获取商家缺货发货后的运单退款金额
     */
    @GetMapping(value = "/remote/expressOrder/getRefundAmountByTradeId/{tradeId}",headers = {"from-source=inner"})
    R<Double> getRefundAmountByTradeId(@PathVariable("tradeId") String tradeId);

    /**
     * 获取运单详情
     */
    @GetMapping(value = "/remote/expressOrder/getInfo/{tradeId}",headers = {"from-source=inner"})
    R<ExpressOrderDTO> getInfo(@PathVariable("tradeId") String tradeId);
}
