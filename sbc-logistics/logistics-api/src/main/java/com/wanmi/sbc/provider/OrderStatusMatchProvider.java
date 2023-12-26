package com.wanmi.sbc.provider;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.logisticsbean.dto.WmsOrderStatusTidDTO;
import com.wanmi.sbc.logisticsbean.dto.WmsOrderStatusTidListDTO;
import com.wanmi.sbc.logisticsbean.vo.WmsOrderStatusVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * Wms订单状态
 * @author lm
 * @date 2022/11/08 17:00
 */
@FeignClient(value = "${application.logistics.name}",contextId = "OrderStatusMatchProvider")
public interface OrderStatusMatchProvider {

    /**
     * 根据订单ID查询wms的订单状态
     * @param request
     * @return
     */
    @PostMapping("/logistics/${application.logistics.version}/orderStatus/queryWmsOrderStatusByTid")
    BaseResponse<WmsOrderStatusVo> queryWmsOrderStatusByTid(@RequestBody @Valid WmsOrderStatusTidDTO request);


    @PostMapping("/logistics/${application.logistics.version}/orderStatus/queryAllWmsOrderStatusByTidList")
    BaseResponse<List<WmsOrderStatusVo>> queryAllWmsOrderStatusByTidList(@RequestBody @Valid WmsOrderStatusTidListDTO request);
}
