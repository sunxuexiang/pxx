package com.wanmi.sbc.order.provider.impl.trade;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.order.api.provider.trade.PileTradeItemProvider;
import com.wanmi.sbc.order.api.request.trade.TradeItemDeleteByCustomerIdRequest;
import com.wanmi.sbc.order.api.request.trade.TradeItemSnapshotRequest;
import com.wanmi.sbc.order.api.request.trade.TradeItemStockOutRequest;
import com.wanmi.sbc.order.trade.model.entity.TradeItem;
import com.wanmi.sbc.order.trade.service.PileTradeItemService;
import com.wanmi.sbc.order.trade.service.PileTradeItemSnapshotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * <p>订单商品服务操作接口</p>
 * @Author: daiyitian
 * @Description: 退单服务操作接口
 * @Date: 2018-12-03 15:40
 */
@Validated
@RestController
public class PileTradeItemController implements PileTradeItemProvider {

    @Autowired
    private PileTradeItemService pileTradeItemService;

    @Autowired
    private PileTradeItemSnapshotService pileTradeItemSnapshotService;

    /**
     * 保存订单商品快照
     *
     * @param request 保存订单商品快照请求结构 {@link TradeItemSnapshotRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @Override
    public BaseResponse snapshot(@RequestBody @Valid TradeItemSnapshotRequest request){
        pileTradeItemService.snapshot(request, KsBeanUtil.convert(request.getTradeItems(), TradeItem.class),
                request.getTradeMarketingList(), request.getSkuList());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 根据customerId删除订单商品快照
     *
     * @param request 根据customerId删除订单商品快照请求结构 {@link TradeItemDeleteByCustomerIdRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @Override
    public BaseResponse deleteByCustomerId(@RequestBody @Valid TradeItemDeleteByCustomerIdRequest request){
        pileTradeItemService.remove(request.getCustomerId());
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse updateUnStock(@Valid TradeItemStockOutRequest request) {
        pileTradeItemService.updateUnStock(request.getCustomerId(), request.getWareId(),request.getStoreId());
        return BaseResponse.SUCCESSFUL();
    }
}
