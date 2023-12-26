package com.wanmi.sbc.order.provider.impl.trade.newPileTrade;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.order.api.provider.trade.newPileTrade.NewPilePickTradeItemQueryProvider;
import com.wanmi.sbc.order.api.request.trade.TradeItemByCustomerIdRequest;
import com.wanmi.sbc.order.api.request.trade.TradeItemSnapshotRequest;
import com.wanmi.sbc.order.api.request.trade.TradeItemStockOutRequest;
import com.wanmi.sbc.order.api.request.trade.newpile.FindPickTradeRequest;
import com.wanmi.sbc.order.api.response.payorder.FindPickTradeResponse;
import com.wanmi.sbc.order.api.response.trade.TradeItemByCustomerIdResponse;
import com.wanmi.sbc.order.api.response.trade.TradeItemStockOutResponse;
import com.wanmi.sbc.order.bean.vo.TradeItemGroupVO;
import com.wanmi.sbc.order.trade.model.entity.TradeItem;
import com.wanmi.sbc.order.trade.model.root.TradeItemGroup;
import com.wanmi.sbc.order.trade.service.newPileTrade.NewPilePickTradeItemService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>订单商品服务查询接口</p>
 * @Author: daiyitian
 * @Description: 退单服务查询接口
 * @Date: 2018-12-03 15:40
 */
@Validated
@RestController
public class NewPilePickTradeItemQueryController implements NewPilePickTradeItemQueryProvider {

    @Autowired
    private NewPilePickTradeItemService newPilePickTradeItemService;

    /**
     * 根据客户id查询已确认订单商品快照
     *
     * @param request 根据客户id查询已确认订单商品快照请求结构 {@link TradeItemByCustomerIdRequest}
     * @return 订单商品快照列表 {@link TradeItemByCustomerIdResponse}
     */
    @Override
    public BaseResponse<TradeItemByCustomerIdResponse> listByCustomerId(@RequestBody @Valid TradeItemByCustomerIdRequest
                                                                                request) {
        List<TradeItemGroup> groupList = newPilePickTradeItemService.find(request.getCustomerId());
        List<TradeItemGroupVO> groupVOList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(groupList)) {
            groupVOList = KsBeanUtil.convert(groupList, TradeItemGroupVO.class);
        }
        return BaseResponse.success(TradeItemByCustomerIdResponse.builder().tradeItemGroupList(groupVOList).build());
    }

    @Override
    public BaseResponse<TradeItemByCustomerIdResponse> listAllByCustomerId(TradeItemByCustomerIdRequest request) {
        List<TradeItemGroup> groupList = newPilePickTradeItemService.findAll(request.getCustomerId());
        List<TradeItemGroupVO> groupVOList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(groupList)) {
            groupVOList = KsBeanUtil.convert(groupList, TradeItemGroupVO.class);
        }
        return BaseResponse.success(TradeItemByCustomerIdResponse.builder().tradeItemGroupList(groupVOList).build());
    }

    @Override
    public BaseResponse<TradeItemStockOutResponse> listStockOutGroupByStoreId(@RequestBody  @Valid TradeItemStockOutRequest request) {
        return BaseResponse.success(TradeItemStockOutResponse.builder()
                .stockOutGroupByStoreId( newPilePickTradeItemService.queryStockNum(request.getCustomerId()
                        , request.getWareId(),request.getStoreId())).build());
    }

    @Override
    public BaseResponse<TradeItemByCustomerIdResponse> itemListByCustomerId(TradeItemByCustomerIdRequest request) {
        List<TradeItemGroup> groupList = newPilePickTradeItemService.findItemByCustomerId(request.getCustomerId());
        List<TradeItemGroupVO> groupVOList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(groupList)) {
            groupVOList = KsBeanUtil.convert(groupList, TradeItemGroupVO.class);
        }
        return BaseResponse.success(TradeItemByCustomerIdResponse.builder().tradeItemGroupList(groupVOList).build());
    }

    @Override
    public BaseResponse snapshotRetail(@Valid TradeItemSnapshotRequest request) {
        newPilePickTradeItemService.snapshotRetail(request, KsBeanUtil.convert(request.getTradeItems(), TradeItem.class),
                request.getTradeMarketingList(), request.getSkuList());
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<FindPickTradeResponse> findPickTradeList(FindPickTradeRequest request) {
        FindPickTradeResponse response = newPilePickTradeItemService.findPickTrade(request);
        return BaseResponse.success(response);
    }
}
