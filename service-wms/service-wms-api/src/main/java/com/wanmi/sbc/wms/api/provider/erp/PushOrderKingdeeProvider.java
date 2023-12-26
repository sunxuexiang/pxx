package com.wanmi.sbc.wms.api.provider.erp;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.wms.api.request.erp.DescriptionFailedQueryStockPushRequest;
import com.wanmi.sbc.wms.api.request.erp.PileTradePushRequest;
import com.wanmi.sbc.wms.api.request.erp.PileTradePushReturnGoodsRequest;
import com.wanmi.sbc.wms.api.response.erp.DescriptionFailedQueryStockPushResponse;
import com.wanmi.sbc.wms.api.response.erp.DescriptionFailedQueryStockPushReturnGoodsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * 推金蝶接口
 *
 * @author yitang
 * @version 1.0
 */
@FeignClient(value = "${application.wms.name}", url="${feign.url.wms:#{null}}", contextId = "PushOrderKingdeeProvider.class" )
public interface PushOrderKingdeeProvider {

    /**
     * 推金蝶囤货销售订单
     * @param tradePushRequest
     */
    @PostMapping("/erp/${application.wms.version}/kingdee/push-sales-kingdee")
    BaseResponse pushSalesKingdee(@RequestBody @Valid PileTradePushRequest tradePushRequest);

    /**
     * 推金蝶退货
     * @param pushReturnGoodsRequest
     */
    @PostMapping("/erp/${application.wms.version}/kingdee/push-return-goods-kingdee")
    BaseResponse pushReturnGoodsKingdee(@RequestBody @Valid PileTradePushReturnGoodsRequest pushReturnGoodsRequest);

    /**
     * 查询推金蝶囤货失败
     * @param stockPushRequest
     * @return
     */
    @PostMapping("/erp/${application.wms.version}/kingdee/find-stock-push-kingdee-order-orders")
    BaseResponse<DescriptionFailedQueryStockPushResponse> findStockPushKingdeeOrderOrders(@RequestBody @Valid DescriptionFailedQueryStockPushRequest stockPushRequest);

    /**
     * 查询推金蝶囤货退货失败
     * @param stockPushRequest
     * @return
     */
    @PostMapping("/erp/${application.wms.version}/kingdee/find-stock-push-kingdee-return-goods-orders")
    BaseResponse<DescriptionFailedQueryStockPushReturnGoodsResponse> findStockPushKingdeeReturnGoodsOrders(@RequestBody @Valid DescriptionFailedQueryStockPushRequest stockPushRequest);

}
